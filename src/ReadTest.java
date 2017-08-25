import model.PinyinTireTree;
import tools.InitialCharacterValue;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class ReadTest {

    public static void main(String[] args) {
        PinyinTireTree tireTree = new PinyinTireTree("config.is", "library.is");
        tireTree.initial();
        System.out.println(System.currentTimeMillis());
        TreeMap<Character, Double> map = tireTree.getCharacters("yi");
        TreeMap<String, Double> wordMap = tireTree.chooseCharacter('异');
        tireTree.chooseWord(PinyinTireTree.NOT_CHOOSE_S);
        System.out.println(System.currentTimeMillis());

        for(Character c : map.keySet()) {
            System.out.println(c + " " + map.get(c));
        }
        for(String s : wordMap.keySet()) {
            System.out.println(s + " " + wordMap.get(s));
        }
    }
}
