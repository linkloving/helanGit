package com.linkloving.helan.logic.UI.main.datachatactivity;

/**
 * Created by Administrator on 2016/7/21.
 */
public class AnylyzeSitResult {
    private int beginIndex;
    private int endIndex;
    private boolean isSleep;
    public int getBeginIndex(){
        return this.beginIndex;
    }
    public void setBeginIndex(int beginIndex){
        this.beginIndex = beginIndex;
    }
    public int getEndIndex(){
        return this.endIndex;
    }
    public void setEndIndex(int endIndex){
        this.endIndex = endIndex;
    }
    public boolean isSleep(){
        return this.isSleep;
    }
    public void setisSleep(boolean isSleep){
        this.isSleep = isSleep;
    }
    @Override
    public String toString() {
        return "AnylyzeResult{" +
                "beginIndex=" + beginIndex +
                ", endIndex=" + endIndex +
                ", isSleep=" + isSleep +
                '}';
    }
}
