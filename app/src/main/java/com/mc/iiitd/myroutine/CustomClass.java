package com.mc.iiitd.myroutine;

/**
 * Created by Nishant on 10/22/2015.
 */
public class CustomClass {

    //private long id;
    private String title, subtitle, icon;
    private int rank;
    private int count;

    public CustomClass () {
    }

    public CustomClass (int c,String icon, String title, String subtitle,int rank) {
        //this.id = id;
        this.title= title;
        this.subtitle= subtitle;
        this.rank=rank;
        this.icon= icon;
        this.count=c;
    }
    public CustomClass (int c, String title, String subtitle,int rank) {
        //this.id = id;
        this.title= title;
        this.subtitle= subtitle;
        this.rank=rank;
        this.count=c;
    }
    //add getters and setters
    public int getCount(){
        return count;
    }
    public void setCount(int c){
        count=c;
    }
    public int getRank(){
        return rank;
    }

    /*public long getId() {
        return id;
    }
    */
    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    /*
    public void setId(long id) {
        this.id = id;
    }
    */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setRank(int ir){
        rank=ir;
    }
}