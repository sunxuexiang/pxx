package com.wanmi.sbc.util;



public class Coordinate {
    private int pageNum;
    private float x;
    private float y;

    public Coordinate(int pageNum, float x, float y) {
        this.pageNum = pageNum;
        this.x = x;
        this.y = y;
    }

    public int getPageNum() {
        return pageNum;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
