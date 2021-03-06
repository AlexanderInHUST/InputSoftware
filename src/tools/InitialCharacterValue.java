package tools;

import model.PinyinTireTree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by tangyifeng on 17/2/20.
 * Email: yifengtang_hust@outlook.com
 */
public class InitialCharacterValue {

    public static void initialValue(String address, String dic, PinyinTireTree tree) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(address));
            String line;
            while ((line = reader.readLine()) != null) {
                for (Character ch : line.toCharArray()) {
                    if (!("《》？：；\"、｜～｀！@＃$％……&＊（）－——＋＝［］｛｝／" +
                            "qwertyuiopasdfghjklzxcvbnm1234567890-_`~,<.>/?;:'\"[{]}\\|=+]")
                            .contains(ch.toString())) {
                        input(ch, dic, tree);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void input(Character ch, String dic, PinyinTireTree tree) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dic));
            String line;
            while ((line = reader.readLine()) != null) {
                String entry[] = line.split(":");
                if(entry[1].contains(ch.toString())) {
                    tree.getCharacters(entry[0]);
                    tree.chooseCharacter(ch);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
