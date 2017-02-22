package tools;

import java.io.*;

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
            while(true) {
                String line = reader.readLine();
                if(line == null) {
                    break;
                }
                String info[] = line.split(":");
                writer.println(info[0] + "," + curAddress + "," + info[1].length());
                curAddress += info[1].length() * 11;
                for(Character c : info[1].toCharArray()) {
                    if(c != ' ') {
                        byte[] ch = c.toString().getBytes();
                        library.write(ch);
                        library.writeDouble(Math.tanh(0.00001));
                    }
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        generate("dic.txt");
    }

}
