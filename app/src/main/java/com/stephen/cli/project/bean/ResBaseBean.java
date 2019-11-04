package com.stephen.cli.project.bean;

import java.io.Serializable;

/**
 * Created by stephen on 11/08/2019.
 */
public class ResBaseBean implements Serializable {
    private int code;
    private String message;

    public int getErrorCode() {
        return code;
    }

    public String getErrMsg() {
        return message;
    }
}
