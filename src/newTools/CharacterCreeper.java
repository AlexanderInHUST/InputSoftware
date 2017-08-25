package newTools;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangyifeng on 2017/8/25.
 * Email: yifengtang_hust@outlook.com
 */
public class CharacterCreeper {

    private static final String ROOT_URL = "http://xh.5156edu.com/pinyi.html";
    private static final String ROOT_HEAD_URL = "http://xh.5156edu.com/";

    private Queue<String> urlQueue;
    private TreeMap<String, HashSet<String>> result;

    public CharacterCreeper() {
        urlQueue = new LinkedBlockingQueue<>();
        result = new TreeMap<>();
    }

    private void creepCharUrl(String data) {
        Pattern pattern = Pattern.compile("href=\"html(.*?)\"");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            String[] groups = matcher.group().split("\"");
            urlQueue.add(ROOT_HEAD_URL + groups[1]);
        }
    }

    private void creepChar(String data) {
        String pinyin;
        HashSet<String> charSet = new HashSet<>();
        Pattern pattern = Pattern.compile("拼音“(.*?)”,找");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()){
            String temp[] = matcher.group().split(">|<");
            pinyin = temp[2];
        } else {
            pinyin = null;
        }
        pattern = Pattern.compile("html'>(.*?)<span>");
        matcher = pattern.matcher(data);
        while(matcher.find()) {
            charSet.add(matcher.group().split("<|>")[1]);
        }
        result.put(pinyin, charSet);
    }

    private String getData(String add){
        try {
            URL url = new URL(add);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gb2312"));
            StringBuilder builder = new StringBuilder();
            String s = "";
            while ((s = reader.readLine()) != null) {
                builder.append(s);
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void saveData() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("dic.txt"));
            for (String key : result.keySet()) {
                writer.print(key + ":");
                for (String value : result.get(key)) {
                    writer.print(value);
                }
                writer.println();
                writer.flush();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CharacterCreeper creeper = new CharacterCreeper();
        String indexData = creeper.getData(ROOT_URL);
        creeper.creepCharUrl(indexData);
        while (!creeper.urlQueue.isEmpty()) {
            String pageData = creeper.getData(creeper.urlQueue.remove());
            creeper.creepChar(pageData);
        }
        creeper.saveData();
    }
}
