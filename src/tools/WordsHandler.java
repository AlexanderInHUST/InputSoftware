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

    public WordsHandler() {
        wordMap = new TreeMap<>();
    }

    public void handle(String address) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(address));
            String word;
            while((word = reader.readLine()) != null) {
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
            for(Character c : wordMap.keySet()) {
                LinkedList<String> wordList = wordMap.get(c);
                for (String s : wordList) {
                    writer.writeUTF(s);
                    writer.writeDouble(Math.tanh(0.001));
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void preHandle(String address) {
        try{
            File file = new File("handleWord.txt");
            BufferedReader reader = new BufferedReader(new FileReader(address));
            PrintWriter writer = new PrintWriter(file);
            String s;
            while((s = reader.readLine()) != null) {
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
