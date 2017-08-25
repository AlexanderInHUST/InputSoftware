package newTools;

import java.io.*;

/**
 * Created by tangyifeng on 2017/8/25.
 * Email: yifengtang_hust@outlook.com
 */
public class DicCleaner {

    private static final String TRASH =
            "1234567890-=~!@#$%&_`qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM;',:<>";
    private static final String S_TRASH =
            "{}[]|:<>,./?-^\\";

    private static String generateString() {
        StringBuilder builder = new StringBuilder();
        for (char c : TRASH.toCharArray()) {
            builder.append(c).append("|");
        }
        for (char c : S_TRASH.toCharArray()) {
            builder.append("\\").append(c).append("|");
        }
        builder.append("\"");
        return builder.toString();
    }

    public static void clear() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("dic.txt"));
            StringBuilder result = new StringBuilder();
            String s;
            while ((s = reader.readLine()) != null) {
                String temp[] = s.split(":");
                String input = "";

                String output = temp[0] + ":";
                for (int i = 1; i < temp.length; i++) {
                    input = input + temp[i];
                }
                String array[] = input.split(generateString());
                for (String s1 : array) {
                    output = output + s1;
                }
                result.append(output);
                result.append("\n");
            }
            reader.close();

            PrintWriter writer = new PrintWriter(new FileWriter("dic.txt"));
            writer.print(result.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DicCleaner.clear();
    }
}
