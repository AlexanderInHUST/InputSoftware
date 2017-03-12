package tools;

import java.nio.ByteBuffer;

/**
 * Created by tangyifeng on 17/3/11.
 * Email: yifengtang_hust@outlook.com
 */
public class BytesTool {

    public static double changeDouble(double d) {
        byte[] output = new byte[8];
        ByteBuffer.wrap(output).putDouble(d);
        for(int i = 0; i < output.length / 2; i++)
        {
            byte temp = output[i];
            output[i] = output[output.length - i - 1];
            output[output.length - i - 1] = temp;
        }
        return ByteBuffer.wrap(output).getDouble();
    }

    public static int changeInt(int i) {
        return Integer.reverseBytes(i);
    }

}
