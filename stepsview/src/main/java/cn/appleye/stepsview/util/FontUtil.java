package cn.appleye.stepsview.util;

import android.graphics.Paint;

/**
 * Created by ubt on 2017/1/23.
 */

public class FontUtil {
    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontLength(Paint paint, String str) {
        return paint.measureText(str);
    }
    /**
     * @return 返回指定笔的文字高度
     */
    public static float getFontHeight(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }
    /**
     * @return 返回指定笔离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading- fm.ascent;
    }
}
