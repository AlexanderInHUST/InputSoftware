package tools;

import java.io.*;
import java.util.HashMap;

/**
 * Created by tangyifeng on 2017/9/6.
 * Email: yifengtang_hust@outlook.com
 */
public class PinyinToDouble {

    private static HashMap<String, String> DOUBLE_MAP;
    private StringBuilder curBuilder;

    public PinyinToDouble() {
        if (DOUBLE_MAP == null) {
            initialMap();
        }
        curBuilder = new StringBuilder();
    }

    private static void initialMap() {
        DOUBLE_MAP = new HashMap<>();
        DOUBLE_MAP.put("ei","q");
        DOUBLE_MAP.put("ian","w");
        DOUBLE_MAP.put("ch","e");
        DOUBLE_MAP.put("er","r");
        DOUBLE_MAP.put("iu","r");
        DOUBLE_MAP.put("iang","t");
        DOUBLE_MAP.put("uang","t");
        DOUBLE_MAP.put("ing","y");
        DOUBLE_MAP.put("uo","o");
        DOUBLE_MAP.put("uan","p");
        DOUBLE_MAP.put("zh","a");
        DOUBLE_MAP.put("iong","s");
        DOUBLE_MAP.put("ong","s");
        DOUBLE_MAP.put("ia","d");
        DOUBLE_MAP.put("ua","d");
        DOUBLE_MAP.put("en","f");
        DOUBLE_MAP.put("eng","g");
        DOUBLE_MAP.put("ang","h");
        DOUBLE_MAP.put("an","j");
        DOUBLE_MAP.put("ao","k");
        DOUBLE_MAP.put("ai","l");
        DOUBLE_MAP.put("iao","z");
        DOUBLE_MAP.put("ie","x");
        DOUBLE_MAP.put("in","c");
        DOUBLE_MAP.put("uai","c");
        DOUBLE_MAP.put("sh","v");
        DOUBLE_MAP.put("ue","v");
        DOUBLE_MAP.put("ou","b");
        DOUBLE_MAP.put("un","n");
        DOUBLE_MAP.put("ue","m");
        DOUBLE_MAP.put("ui","m");
    }

    public void change() {
        try {
            File dic = new File("dic.txt");
            File dicD = new File("dic_d.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dic)));
            PrintWriter writer = new PrintWriter(dicD);
            String buf = "";
            while ((buf = reader.readLine()) != null) {
                String bufArrays[] = buf.split(":");
                String newLine = changeString(bufArrays[0]) + ":" + bufArrays[1];
                writer.println(newLine);
            }
            writer.flush();
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String changeString(String s) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= s.length(); i++) {
            String preString = s.substring(0, i);
            String afterString = s.substring(i, s.length());
            if (DOUBLE_MAP.keySet().contains(preString)) {
                builder.append(DOUBLE_MAP.get(preString));
                if (DOUBLE_MAP.keySet().contains(afterString)) {
                    builder.append(DOUBLE_MAP.get(afterString));
                } else {
                    builder.append(afterString);
                }
                break;
            } else if (DOUBLE_MAP.keySet().contains(afterString)) {
                builder.append(preString);
                builder.append(DOUBLE_MAP.get(afterString));
                break;
            }
        }
        return (builder.toString().isEmpty()) ? s : builder.toString();
    }

    public static void main(String[] args) {
        PinyinToDouble pinyinToDouble = new PinyinToDouble();
        pinyinToDouble.change();
    }
}
