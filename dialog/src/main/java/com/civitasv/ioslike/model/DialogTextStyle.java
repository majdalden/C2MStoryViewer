package com.civitasv.ioslike.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.civitasv.dialog.R;


/**
 * @author Civitasv
 * 底部弹窗条目样式 构造者模式
 * 2020-11-21
 */
public final class DialogTextStyle {
    private final Context context;

    private final int color; // 颜色

    private final float textSize; // 字大

    private final boolean textAllCaps; // 字大

    private final Typeface typeface; // 样式

    private DialogTextStyle(Builder builder) {
        context = builder.context;
        color = builder.color;
        textSize = builder.textSize;
        textAllCaps = builder.textAllCaps;
        typeface = builder.typeface;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public int getColor() {
        return color;
    }

    public float getTextSize() {
        return textSize;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public static class Builder {
        private final Context context;
        // 可选参数
        private int color;
        private float textSize; // 单位：sp
        private boolean textAllCaps; // 单位：sp
        private Typeface typeface;

        public Builder(Context context) {
            this.context = context;
            this.color = ContextCompat.getColor(context, R.color.black);  // 黑色
            this.textSize = 16;
            this.textAllCaps = false;
            this.typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        }

        public Builder color(String color) {
            this.color = Color.parseColor(color);
            return this;
        }

        public Builder color(@ColorRes int color) {
            this.color = ContextCompat.getColor(context, color);
            return this;
        }

        public Builder textSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder textAllCaps(boolean textAllCaps) {
            this.textAllCaps = textAllCaps;
            return this;
        }

        public Builder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public DialogTextStyle build() {
            return new DialogTextStyle(this);
        }
    }
}
