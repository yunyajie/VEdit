package com.example.vedit.Utils;

import android.content.res.Resources;

/**
 * @ProjectName: EpMediaTest
 * @Package: com.example.epmediatest.Utils
 * @ClassName: DimensionUtil
 * @Description: java类作用描述
 * @Author: yunyajie
 * @CreateDate: 2020/4/14 13:31
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/14 13:31
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DimensionUtil {
    public static int dpTopx(int dp){
        return (int)(dp* Resources.getSystem().getDisplayMetrics().density);
    }
}
