package tools;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by tangyifeng on 17/2/20.
 * Email: yifengtang_hust@outlook.com
 */
public class MainGenerator {

    public static void generate(String d) {
        try {
            File config = new File("config.is");
            RandomAccessFile library = new RandomAccessFile("library.is", "rw");
            BufferedReader reader = new BufferedReader(new FileReader(d));
            PrintWriter writer = new PrintWriter(config);
            int curAddress = 0;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String info[] = line.split(":");
                writer.println(info[0] + "," + curAddress + "," + info[1].length());
                curAddress += info[1].length() * 19;
                for (Character c : info[1].toCharArray()) {
                    if (c != ' ') {
                        byte[] ch = c.toString().getBytes();
                        library.write(ch);
                        library.writeDouble(BytesTool.changeDouble(Math.tanh(0.001)));
                        int wordInfo[] = getCharNum(c);
                        library.writeInt(BytesTool.changeInt(wordInfo[0]));
                        library.writeInt(BytesTool.changeInt(wordInfo[1]));
                        System.out.println(c + " " + wordInfo[0] + " " + wordInfo[1]);
                    }
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int[] getCharNum(Character c) {
        int pos = 0;
        int info[] = new int[2];
        try {
            RandomAccessFile file = new RandomAccessFile("wordsDic.txt", "r");
            String utf;
            while ((utf = file.readUTF()) != null && !utf.isEmpty()) {
                if (c.equals(utf.charAt(0))) {
                    info[0] = pos;
                    int count = 1;
                    while (true) {
                        file.skipBytes(8);
                        utf = file.readUTF();
                        if (c.equals(utf.charAt(0))) {
                            count++;
                        } else {
                            info[1] = count;
                            return info;
                        }
                    }
                } else {
                    pos = pos + 2 + utf.length() * 3 + 8;
                    file.seek(pos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(c);
        }
        info[0] = -1;
        info[1] = -1;
        return info;
    }

    public static void main(String[] args) {
        generate("dic.txt");
    }

}
