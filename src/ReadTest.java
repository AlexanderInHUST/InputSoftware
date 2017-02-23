import model.PinyinTireTree;
import tools.InitialCharacterValue;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
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
        InitialCharacterValue.initialValue("three_country_story.txt", "dic.txt", tireTree);
        TreeMap<Character, Double> map = tireTree.getCharacters("w");
        tireTree.chooseCharacter(PinyinTireTree.NOT_CHOOSE);
        for(Character c : map.keySet()) {
            System.out.println(c + " " + map.get(c));
        }
    }

}
