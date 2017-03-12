package tools;

/**
 * Created by tangyifeng on 17/3/11.
 * Email: yifengtang_hust@outlook.com
 */
public class BytesTool {

    public static double changeDouble(double d) {
        long l = Double.doubleToLongBits(d);
        l = Long.reverseBytes(l);
        return Double.longBitsToDouble(l);
    }

    public static int changeInt(int i) {
        return Integer.reverseBytes(i);
    }

}
