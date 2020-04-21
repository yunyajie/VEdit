package com.example.vedit.Utils;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: TimerUtils
 * @Description: 定时器工具类
 * @Author: yunyajie
 * @CreateDate: 2020/3/30 18:46
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/30 18:46
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TimerUtils {
    //定时器
    private Timer timer;
    //定时器任务栈
    private TimerTask timerTask;
    //通知Handler
    private Handler handler;
    //通知类型
    private int notifyWhat;
    //延迟时间-多少豪秒后开始执行
    private long delay;
    //循环时间-每个多少秒执行一次
    private long period;
    //触发次数上限
    private int triggerLimit=1;
    //触发次数
    private int triggerNumber=0;
    public TimerUtils() {
        super();
    }

    public TimerUtils(Handler handler) {
        super();
        this.handler = handler;
    }


    /**
     * 定时器操作
     * @param isOpen 是否打开
     */
    private void timerOperate(boolean isOpen){
        if (isOpen) {
            // 每次重置触发次数
            triggerNumber = 0;
            try {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
            } catch (Exception e) {
            }
            // 开启定时器
            timer = new Timer(); // 每次重新new 防止被取消
            // 重新生成定时器 防止出现TimerTask is scheduled already 所以同一个定时器任务只能被放置一次
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    // 先进行通知
                    if(handler != null){
                        handler.sendEmptyMessage(notifyWhat);
                    }
                    // 累积触发次数
                    triggerNumber++;
//                    // 如果大于触发次数,则关闭
//                    if(triggerNumber >= triggerLimit && triggerLimit >= 0){
//                        // 进行关闭
//                        timerOperate(false);
//                    }
                }
            };
            try {
                // xx毫秒后执行，每隔xx毫秒再执行一次
                timer.schedule(timerTask, delay, period);
            } catch (Exception e) {
            }
        } else {
            try {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
            } catch (Exception e) {
            }
        }
    }


    // ================ 对外公开方法   =====================
    /**
     * 设置通知的Handler
     * @param handler
     */
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 设置通知的What
     * @param notifyWhat
     */
    public void setNotifyWhat(int notifyWhat) {
        this.notifyWhat = notifyWhat;
    }

    /**
     * 设置时间
     * @param delay 延迟时间 - 多少毫秒后开始执行
     * @param period 循环时间 - 每隔多少秒执行一次
     */
    public void setTime(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    /**
     * 设置触发次数上限
     * @param triggerLimit
     */
    public void setTriggerLimit(int triggerLimit) {
        this.triggerLimit = triggerLimit;
    }


    // ========================

    /** 开始定时 */
    public void startTimer(){
        timerOperate(true);
    }

    /** 关闭定时 */
    public void closeTimer(){
        timerOperate(false);
    }
}
