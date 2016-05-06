package com.ancientlore.sapper;

import android.graphics.Rect;

class GameObject {
    private int index;
    private int x,y;
    private int minesAround;
    private boolean mined;
    private GOState state;
    private Rect rect;

    GameObject(int x,int y,Rect rect){
        this.x=x;
        this.y=y;
        this.index = 0;
        minesAround=0;
        mined = false;
        this.state = GOState.CLOSED;
        this.rect = rect;
    }
    GameObject(int index, GOState state, Rect rect){
        this.index = index;
        this.state = state;
        this.rect = rect;
    }

    public int getIndex() {return index;}
    public Rect getRect() {return rect;}
    public GOState getState() {return state;}
    public boolean isMined() {return mined;}
    public int getMinesAround() {return minesAround;}
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }


    public void setIndex(int index) {this.index = index;}
    public void setRect(Rect rect) {this.rect = rect;}
    public void setState(GOState state) {this.state = state;}
    public void setMined(boolean mined) {this.mined = mined;}
    public void setMinesAround(int minesAround) {this.minesAround = minesAround;}
    public void addMinesAround() {this.minesAround ++;}
}
