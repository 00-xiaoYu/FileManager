package com.filemanager.fm.model;

public class Progress {
    private long ReadBytes; //已读取文件的比特数
    private long contentLength;//文件总比特数
    private long items; //正读的第几个文件

    public long getReadBytes() {
        return ReadBytes;
    }

    public void setReadBytes(long readBytes) {
        ReadBytes = readBytes;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getItems() {
        return items;
    }

    public void setItems(long items) {
        this.items = items;
    }

    public void setAllBytes(long allBytes) {
    }

    public void setCurrentIndex(int index) {
    }
}
