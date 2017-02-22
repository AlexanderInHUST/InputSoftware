package tools;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by tangyifeng on 17/2/20.
 * Email: yifengtang_hust@outlook.com
 */
public class FormatChanger {

    public static void change(String address) {
        try{
            File file = new File(address);
            File result = new File("result.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(new FileWriter(result));
            String text[] = reader.readLine().split(" ");
            boolean finishLine = false;
            String cur = "";
            ArrayList<String> pinyin = new ArrayList<>();
            ArrayList<String> chars = new ArrayList<>();
            for(int i = 0; i < text.length; i++) {
                if(text[i].charAt(0) >= 'a' && text[i].charAt(0) <= 'z') {
                    if(finishLine) {
                        if(pinyin.size() != chars.size()) {
                            writer.println();
                            for(String py : pinyin) {
                                writer.print(py.trim() + " ");
                            }
                            writer.println();
                            for(String ch : chars) {
                                writer.print(ch.trim() + " ");
                            }
                            writer.println();
                            finishLine = false;
                        } else {
                            if(cur.isEmpty()) {
                                cur = pinyin.get(0);
                            }
                            for(int j = 0; j < pinyin.size(); j++) {
                                if(!cur.equals(pinyin.get(j))) {
                                    writer.println();
                                    writer.print(pinyin.get(j).trim() + ":");
                                    cur = pinyin.get(j);
                                }
                                writer.print(chars.get(j).trim() + ",");
                            }
                        }
                        pinyin = new ArrayList<>();
                        chars = new ArrayList<>();
                        finishLine = false;
                    }
                    pinyin.add(text[i]);
                } else {
                    finishLine = true;
                    chars.add(text[i]);
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }

}
