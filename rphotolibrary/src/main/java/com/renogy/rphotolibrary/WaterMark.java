package com.renogy.rphotolibrary;

import android.graphics.Color;

import android.text.TextUtils;

import com.blankj.utilcode.util.TimeUtils;

import java.text.SimpleDateFormat;

import androidx.annotation.ColorInt;

public class WaterMark {
    //文字的标志
    private String contentMark = "";
    //默认的日期格式
    private String imgWaterDate = "yyyy-MM-dd HH:mm:ss";
    //文字的大小sp
    private float textSize = 6;
    //文字的颜色
    private int textColor;
    //文字的位置X 暂时不用
    private float x;
    //文字的位置Y 暂时不用
    private float y;
    //所有位置1.左上，2，左下，3.右上，4，右下
    private Integer loacation = 4;


    public WaterMark() {
        this("yyyy-MM-dd HH:mm:ss", 6f, Color.RED, 8, 8, 4);
    }

    public WaterMark(float textSize, @ColorInt int textColor) {
        this("yyyy-MM-dd HH:mm:ss", textSize, textColor);
    }

    public WaterMark(String imgWaterDate, float textSize, @ColorInt int textColor) {
        this(imgWaterDate, textSize, textColor, 8f, 8f, 4);
    }

    /**
     * @param imgWaterDate 水印的日期格式 默认为 "yyyy-MM-dd HH:mm:ss"
     * @param textSize     字体大小
     * @param textColor    字体颜色
     * @param x            水印x向左平移
     * @param y            水印y向下平移
     * @param loacation    位置
     */
    public WaterMark(String imgWaterDate, float textSize, @ColorInt int textColor, float x, float y, Integer loacation) {
        this.imgWaterDate = imgWaterDate;
        this.textSize = textSize;
        this.textColor = textColor;
        this.x = x;
        this.y = y;
        this.loacation = loacation;
    }

    public String getImgWaterDate() {
        if (TextUtils.isEmpty(imgWaterDate)) return "";
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        dateFormat.applyPattern(imgWaterDate);
        return TimeUtils.getNowString(dateFormat);
    }

    public float getTextSize() {
        return textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Integer getLoacation() {
        return loacation;
    }
}