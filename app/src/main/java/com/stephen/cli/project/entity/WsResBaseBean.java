package com.stephen.cli.project.entity;

import java.io.Serializable;

public class WsResBaseBean implements Serializable {
    private String method;
    private String errMsg;
    private int errorCode;// 非 0 位错误，可以直接显示 Msg 中的类型给予用户。(一般有 系统级异常，服务端都会主动踢掉该用户，前端根据错误码判断是否尝试连接)

    public String getMethod() {
        return method;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
