package tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * Created by tangyifeng on 17/3/6.
 * Email: yifengtang_hust@outlook.com
 */
public class WordsHandler {

    private TreeMap<Character, LinkedList<String>> wordMap;
    private long allCount = 0;

    public WordsHandler() {
        wordMap = new TreeMap<>();
    }

    public void handle(String address) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(address));
            String word;
            HashMap<String, Integer> map = getWordValueMap();
            int num = 0;
            System.out.println(allCount);
            while ((word = reader.readLine()) != null) {
                LinkedList<String> wordList;
                if (wordMap.containsKey(word.charAt(0))) {
                    wordList = wordMap.get(word.charAt(0));
                } else {
                    wordList = new LinkedList<>();
                }
                wordList.add(word);
                wordMap.put(word.charAt(0), wordList);
            }
            RandomAccessFile writer = new RandomAccessFile("wordsDic.txt", "rw");
            for (Character c : wordMap.keySet()) {
                LinkedList<String> wordList = wordMap.get(c);
                for (String s : wordList) {
                    if (map.keySet().contains(s)) {
                        int value = map.get(s);
                        if (value > 300) {
                            writer.writeUTF(s);
                            writer.writeDouble(Math.tanh((double) value / (double) allCount * 1000));
                            System.out.println(s + " " + (double) value / (double) allCount * 1000);
                            num++;
                        }
                    }
                }
            }
            System.out.println(num);
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, Integer> getWordValueMap() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("30w.txt"));
            HashMap<String, Integer> map = new HashMap<>();
            String line;
            while((line = reader.readLine()) != null) {
                String info[] = line.split("\t|(  )");
                String word = info[1];
                Integer value = Integer.parseInt(info[2]);
                allCount = allCount + value;
                map.put(word, value);
            }
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void preHandle(String address) {
        try {
            File file = new File("handleWord.txt");
            BufferedReader reader = new BufferedReader(new FileReader(address));
            PrintWriter writer = new PrintWriter(file);
            String s;
            while ((s = reader.readLine()) != null) {
                if (s.length() < 5) {
                    writer.println(s);
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WordsHandler handler = new WordsHandler();
        handler.handle("smallDic.txt");
    }

}
