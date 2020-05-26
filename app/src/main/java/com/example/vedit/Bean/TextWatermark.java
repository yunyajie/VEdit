package com.example.vedit.Bean;

import VideoHandle.EpText;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Bean
 * @ClassName: TextWatermark
 * @Description: 文字水印
 * @Author: yunyajie
 * @CreateDate: 2020/5/26 16:48
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/26 16:48
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TextWatermark {
    private int textX;
    private int textY;
    private EpText.Color textColor;
    private float textSize;
    private String content;
    private int startTime;
    private int endTime;

    public TextWatermark(int textX, int textY, EpText.Color textColor, float textSize, String content, int startTime, int endTime) {
        this.textX = textX;
        this.textY = textY;
        this.textColor = textColor;
        this.textSize = textSize;
        this.content = content;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getTextX() {
        return textX;
    }

    public int getTextY() {
        return textY;
    }

    public EpText.Color getTextColor() {
        return textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public String getContent() {
        return content;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
}
