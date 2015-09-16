package com.sakurafish.parrot.callconfirm.dto;

import java.util.List;

/**
 * アプリからのメッセージ
 * Created by sakura on 9/16/15.
 */
public class AppMessage {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        private String app;
        private int version;
        private int message_no;
        private String message;

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getMessage_no() {
            return message_no;
        }

        public void setMessage_no(int message_no) {
            this.message_no = message_no;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
