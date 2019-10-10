package com.example.photogalleryapp;

public class PhotoClass {

    private String fpath;
    private String caption;
    private String timeStamp;
    private String fileName;

    final private String DEFAULT_CAPTION = "This is a sick caption!";
    final private String DEFAULT_TS = "NO TIMESTAMP";

    public PhotoClass(String fp) {
        this.fpath = fp;
        this.fileName = fp.substring(fp.lastIndexOf("/")+1);;
        this.caption = DEFAULT_CAPTION;
        this.timeStamp = DEFAULT_TS;
    }

    public void setCaption(String c) {
        this.caption = c;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setPath(String p) {
        this.fpath = p;
    }

    public String getPath() {
        return this.fpath;
    }

    public void setTimeStamp(String ts) {
        this.timeStamp = ts;
    }

    public String getTimeStamp() {
        return this.timeStamp;
    }

    public void setFileName(String fn) {
        this.fileName = fn;
    }
    public String getFileName() {
        return this.fileName;
    }
}
