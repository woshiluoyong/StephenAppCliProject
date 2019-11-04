package com.stephen.cli.project.scoket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author rabtman
 */

public class WsManager implements IWsManager {
  private final static int RECONNECT_INTERVAL = 5 * 1000;    //重连自增步长
  private final static long RECONNECT_MAX_TIME = 120 * 1000;   //最大重连间隔
  private Context mContext;
  private String wsUrl;
  private WebSocket mWebSocket;
  private OkHttpClient mOkHttpClient;
  private Request mRequest;
  private int mCurrentStatus = WsStatus.DISCONNECTED;     //websocket连接状态
  private boolean isNeedReconnect;          //是否需要断线自动重连
  private boolean isManualClose = false;         //是否为手动关闭websocket连接
  private WsStatusListener wsStatusListener;
  private Lock mLock;
  private Handler wsMainHandler = new Handler(Looper.getMainLooper());
  private int reconnectCount = 0;   //重连次数
  private NetworkChangeReceiver networkChangeReceiver;

  public WsManager(Builder builder) {
    mContext = builder.mContext;
    wsUrl = builder.wsUrl;
    isNeedReconnect = builder.needReconnect;
    mOkHttpClient = builder.mOkHttpClient;
    this.mLock = new ReentrantLock();
  }

  private void initWebSocket() {
    if (mOkHttpClient == null)mOkHttpClient = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    if (mRequest == null) mRequest = new Request.Builder().url(wsUrl).build();
    mOkHttpClient.dispatcher().cancelAll();
    try {
      mLock.lockInterruptibly();
      try {
        mOkHttpClient.newWebSocket(mRequest, mWebSocketListener);
      } finally {
        mLock.unlock();
      }
    } catch (InterruptedException e) {}
  }

  @Override
  public WebSocket getWebSocket() {
    return mWebSocket;
  }

  public void setWsStatusListener(WsStatusListener wsStatusListener) {
    this.wsStatusListener = wsStatusListener;
  }

  @Override
  public synchronized boolean isWsConnected() {
    return mCurrentStatus == WsStatus.CONNECTED;
  }

  @Override
  public synchronized int getCurrentStatus() {
    return mCurrentStatus;
  }

  @Override
  public synchronized void setCurrentStatus(int currentStatus) {
    this.mCurrentStatus = currentStatus;
  }

  @Override
  public void startConnect() {
    isManualClose = false;
    buildConnect();
    registerNetworkChangeReceiver();
  }

  @Override
  public void stopConnect() {
    isManualClose = true;
    disconnect();
  }

  private void tryReconnect() {
    if (!isNeedReconnect | isManualClose) return;

    if (!isNetworkConnected(mContext)) {
      setCurrentStatus(WsStatus.DISCONNECTED);
      return;
    }// end of if

    setCurrentStatus(WsStatus.RECONNECT);

    long delay = reconnectCount * RECONNECT_INTERVAL;
    wsMainHandler.postDelayed(reconnectRunnable, delay > RECONNECT_MAX_TIME ? RECONNECT_MAX_TIME : delay);
    reconnectCount++;
  }

  private void cancelReconnect() {
    if(null != wsMainHandler && null != reconnectRunnable)wsMainHandler.removeCallbacks(reconnectRunnable);
    reconnectCount = 0;
  }

  private void connected() {
    cancelReconnect();
  }

  private void disconnect() {
    if (mCurrentStatus == WsStatus.DISCONNECTED) return;
    cancelReconnect();
    if (mOkHttpClient != null) mOkHttpClient.dispatcher().cancelAll();
    if (mWebSocket != null) {
      boolean isClosed = mWebSocket.close(WsStatus.CODE.NORMAL_CLOSE, WsStatus.TIP.NORMAL_CLOSE);
      if (!isClosed) {//非正常关闭连接
        if (wsStatusListener != null) wsStatusListener.onClosed(WsStatus.CODE.ABNORMAL_CLOSE, WsStatus.TIP.ABNORMAL_CLOSE);
      }// end of if
    }// end of if
    setCurrentStatus(WsStatus.DISCONNECTED);
    unRegisterNetworkChangeReceiver();
  }

  private synchronized void buildConnect() {
    if (!isNetworkConnected(mContext)) {
      setCurrentStatus(WsStatus.DISCONNECTED);
      return;
    }// end of if
    switch (getCurrentStatus()) {
      case WsStatus.CONNECTED:
      case WsStatus.CONNECTING:
        break;
      default:
        setCurrentStatus(WsStatus.CONNECTING);
        initWebSocket();
    }// end of switch
  }

  //发送消息
  @Override
  public boolean sendMessage(String msg) {
    return send(msg);
  }

  @Override
  public boolean sendMessage(ByteString byteString) {
    return send(byteString);
  }

  private boolean send(Object msg) {
    boolean isSend = false;
    if (mWebSocket != null && mCurrentStatus == WsStatus.CONNECTED) {
      if (msg instanceof String) {
        isSend = mWebSocket.send((String) msg);
      } else if (msg instanceof ByteString) {
        isSend = mWebSocket.send((ByteString) msg);
      }
      if (!isSend) tryReconnect();//发送消息失败，尝试重连
    }// end of if
    return isSend;
  }

  private Runnable reconnectRunnable = new Runnable() {
    @Override
    public void run() {
      if (wsStatusListener != null) wsStatusListener.onReconnect();
      buildConnect();
    }
  };

  private WebSocketListener mWebSocketListener = new WebSocketListener() {

    @Override
    public void onOpen(WebSocket webSocket, final Response response) {
      mWebSocket = webSocket;
      setCurrentStatus(WsStatus.CONNECTED);
      connected();
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onOpen(response);
            }
          });
        } else {
          wsStatusListener.onOpen(response);
        }
      }
    }

    @Override
    public void onMessage(WebSocket webSocket, final ByteString bytes) {
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onMessage(bytes);
            }
          });
        } else {
          wsStatusListener.onMessage(bytes);
        }
      }
    }

    @Override
    public void onMessage(WebSocket webSocket, final String text) {
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onMessage(text);
            }
          });
        } else {
          wsStatusListener.onMessage(text);
        }
      }
    }

    @Override
    public void onClosing(WebSocket webSocket, final int code, final String reason) {
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onClosing(code, reason);
            }
          });
        } else {
          wsStatusListener.onClosing(code, reason);
        }
      }
    }

    @Override
    public void onClosed(WebSocket webSocket, final int code, final String reason) {
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onClosed(code, reason);
            }
          });
        } else {
          wsStatusListener.onClosed(code, reason);
        }
      }
    }

    @Override
    public void onFailure(WebSocket webSocket, final Throwable t, final Response response) {
      tryReconnect();
      if (wsStatusListener != null) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
          wsMainHandler.post(new Runnable() {
            @Override
            public void run() {
              if(null != wsStatusListener)wsStatusListener.onFailure(t, response);
            }
          });
        } else {
          wsStatusListener.onFailure(t, response);
        }
      }
    }
  };

  //检查网络是否连接
  private boolean isNetworkConnected(Context context) {
    if (context != null) {
      ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
      if (mNetworkInfo != null) return mNetworkInfo.isAvailable();
    }// end of if
    return false;
  }

  private void registerNetworkChangeReceiver(){
    try {
      unRegisterNetworkChangeReceiver();
      networkChangeReceiver = new NetworkChangeReceiver();
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
      if(null != mContext && null != networkChangeReceiver)mContext.registerReceiver(networkChangeReceiver, intentFilter);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void unRegisterNetworkChangeReceiver(){// 注意避免java.lang.IllegalArgumentException : Receiver not registered异常
    try { if(null != mContext && null != networkChangeReceiver)mContext.unregisterReceiver(networkChangeReceiver); } catch (Exception e) { e.printStackTrace(); }
  }

  class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
      System.out.println("=======WebSocket========NetworkChangeReceiver===广播=>");
      cancelReconnect();
      if(null == mContext)return;
      ConnectivityManager connectionManager=(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  //得到系统服务类
      NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
      if(null != networkInfo && networkInfo.isAvailable()){
        System.out.println("=======WebSocket========NetworkChangeReceiver===网络可用了=>");
        tryReconnect();
      }else{
        System.out.println("=======WebSocket========NetworkChangeReceiver===网络不可用=>");
      }
    }
  }

  public static final class Builder {
    private Context mContext;
    private String wsUrl;
    private boolean needReconnect = true;
    private OkHttpClient mOkHttpClient;

    public Builder(Context val) {
      mContext = val;
    }

    public Builder wsUrl(String val) {
      wsUrl = val;
      return this;
    }

    public Builder client(OkHttpClient val) {
      mOkHttpClient = val;
      return this;
    }

    public Builder needReconnect(boolean val) {
      needReconnect = val;
      return this;
    }

    public WsManager build() {
      return new WsManager(this);
    }
  }
}
