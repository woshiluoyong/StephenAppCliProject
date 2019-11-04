package com.stephen.cli.project.entity;

import java.io.Serializable;

public class WsReqLoginBean extends WsReqBaseBean implements Serializable {
    private Data body;

    public WsReqLoginBean(String token) {
        super("login");
        this.body = new Data(token);
    }

    public class Data {
        private String token;
        private String clientFlag = "app";

        public Data(String token) {
            this.token = token;
        }
    }
}
