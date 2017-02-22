package tools;

import java.io.*;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * Created by tangyifeng on 17/2/20.
 * Email: yifengtang_hust@outlook.com
 */
public class DicGenerator {

    private TreeMap<String, HashSet<Character>> charMap;
    private File dic;
    private File result;

    public DicGenerator(String dicAddress, String resultAddress) {
        this.dic = new File(dicAddress);
        this.result = new File(resultAddress);
        this.charMap = new TreeMap<>();
    }

    public void loadDic() {
        try {
            charMap = new TreeMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(dic));
            while (true) {
                String line = reader.readLine();
                if (line == null)
                    break;
                String info[] = line.split(":");
                String chars[] = info[1].split(",");
                HashSet<Character> chs;
                if (charMap.containsKey(info[0])) {
                    chs = charMap.get(info[0]);
                } else {
                    chs = new HashSet<>();
                }
                for(String c : chars) {
                    if(!c.isEmpty()) {
                        chs.add(c.trim().charAt(0));
                    }
                }
                charMap.put(info[0], chs);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDic() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(result));
            for(String key : charMap.keySet()) {
                writer.print(key + ":");
                for(Character c : charMap.get(key)) {
                    if(c != '\u007F')
                     writer.print(c);
                }
                writer.println();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DicGenerator generator = new DicGenerator("result.txt", "dic.txt");
        generator.loadDic();
        generator.writeDic();
        System.out.println();
    }

}
