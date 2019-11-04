package com.stephen.cli.project.bean;

import java.util.List;

/**
 * Created by stephen on 13/08/2019.
 */

public class ResMainBean extends ResBaseBean{
    private int totalPage = 3;
    private List<Data> result;

    public int getTotalPage() {
        return totalPage;
    }

    public List<Data> getResult() {
        return result;
    }

    public class Data{
        private String author;
        private String link;
        private String pic;
        private String type;
        private String title;
        private String lrc;
        private long songid;
        private String url;

        public String getAuthor() {
            return author;
        }

        public String getLink() {
            return link;
        }

        public String getPic() {
            return pic;
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getLrc() {
            return lrc;
        }

        public long getSongid() {
            return songid;
        }

        public String getUrl() {
            return url;
        }
    }
}
