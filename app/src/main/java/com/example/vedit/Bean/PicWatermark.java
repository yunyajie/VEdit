package com.example.vedit.Bean;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Bean
 * @ClassName: PicWatermark
 * @Description: 添加的图片水印的信息
 * @Author: yunyajie
 * @CreateDate: 2020/5/26 11:01
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/26 11:01
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PicWatermark {
    private int picX;
    private int picY;
    private float picWidth;
    private float picHeight;
    private String picPath;
    private int startTime;
    private int endTime;
    public PicWatermark(int picX,int picY,float picWidth,float picHeight,String picPath,int startTime,int endTime) {
        this.picX=picX;
        this.picY=picY;
        this.picWidth=picWidth;
        this.picHeight=picHeight;
        this.picPath=picPath;
        this.startTime=startTime;
        this.endTime=endTime;
    }

    public int getPicX() {
        return picX;
    }

    public int getPicY() {
        return picY;
    }

    public float getPicWidth() {
        return picWidth;
    }

    public float getPicHeight() {
        return picHeight;
    }

    public String getPicPath() {
        return picPath;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
}
