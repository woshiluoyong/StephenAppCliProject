package com.stephen.cli.project.entity;

import java.io.Serializable;

public class WsReqBaseBean implements Serializable {
    private String method;

    public WsReqBaseBean(String method) {
        this.method = method;
    }
}
