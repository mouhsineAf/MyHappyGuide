package com.devm22.happyguide.model;

import java.util.Date;

public class Tips {
    private int tipId;
    private String tipName;
    private String tipImage;
    private String tipContent;
    private Date tipDate;
    private float tipRate;
    private boolean tipFavorite;

    public Tips(int tipId, String tipName, String tipImage, String tipContent, Date tipDate, float tipRate, boolean tipFavorite) {
        this.tipId = tipId;
        this.tipName = tipName;
        this.tipImage = tipImage;
        this.tipContent = tipContent;
        this.tipDate = tipDate;
        this.tipRate = tipRate;
        this.tipFavorite = tipFavorite;
    }

    public int getTipId() {
        return tipId;
    }

    public void setTipId(int tipId) {
        this.tipId = tipId;
    }

    public String getTipName() {
        return tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName;
    }

    public String getTipImage() {
        return tipImage;
    }

    public void setTipImage(String tipImage) {
        this.tipImage = tipImage;
    }

    public String getTipContent() {
        return tipContent;
    }

    public void setTipContent(String tipContent) {
        this.tipContent = tipContent;
    }

    public Date getTipDate() {
        return tipDate;
    }

    public void setTipDate(Date tipDate) {
        this.tipDate = tipDate;
    }

    public float getTipRate() {
        return tipRate;
    }

    public void setTipRate(float tipRate) {
        this.tipRate = tipRate;
    }

    public boolean isTipFavorite() {
        return tipFavorite;
    }

    public void setTipFavorite(boolean tipFavorite) {
        this.tipFavorite = tipFavorite;
    }
}
