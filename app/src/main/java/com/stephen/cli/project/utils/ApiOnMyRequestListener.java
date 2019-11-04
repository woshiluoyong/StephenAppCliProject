package com.stephen.cli.project.utils;

import com.stephen.cli.project.bean.ResBaseBean;

import org.json.JSONObject;

public abstract class ApiOnMyRequestListener implements ApiRequestTool.onRequestListener {
    @Override
    public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {}

    @Override
    public boolean isFailShowMsg() {
        return true;
    }

    @Override
    public void requestCallFail(boolean isError, int errorCode, String errorMsg) {}
}