package com.example.thunder.download;

/**
 * desc:
 * author: daiwj on 2019-11-01 22:29
 */
public class UrlInfo {

    private String name;
    private String url;

    public UrlInfo(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
