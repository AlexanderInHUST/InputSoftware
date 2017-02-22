import model.PinyinTireTree;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class ReadTest {

    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        PinyinTireTree tireTree = new PinyinTireTree("config.is", "library.is");
        tireTree.initial();
        ArrayList<Character> list = tireTree.getCharacters("zi");
        System.out.println(System.currentTimeMillis());
        for(Character c : list) {
            System.out.println(c);
        }
    }

}
