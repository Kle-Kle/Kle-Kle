package com.example.klekle.domain;

import java.io.Serializable;

public class Hold implements Serializable {

    private int xmax;
    private int ymax;

    public Hold(int xmax, int ymax) {
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public int getXmax() {
        return xmax;
    }

    public int getYmax() {
        return ymax;
    }
}


