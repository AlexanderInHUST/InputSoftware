package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class PinyinTireTree {

    private PinyinNode root;
    private File config;
    private RandomAccessFile dic;
    private PinyinNode curNode;
    private ArrayList<Character> curChars;
    private ArrayList<Double> curValue;
    private ValueComparator comparator;

    public PinyinTireTree(String config, String dic) {
        try {
            this.config = new File(config);
            this.dic = new RandomAccessFile(dic, "rw");
            comparator = new ValueComparator();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initial() {
        TreeMap<String, String> pinyinConfig = new TreeMap<>();
        root = new PinyinNode();
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(config));
            String singleConfig;
            while ((singleConfig = configReader.readLine()) != null) {
                String info[] = singleConfig.split(",");
                // eg. (a,1,20) refers to (pinyin, address of start, num of character),
                // please check config.is to know more
                pinyinConfig.put(info[0], info[1] + "," + info[2]);
            }
            for (String key : pinyinConfig.keySet()) {
                addNode(root, key, pinyinConfig.get(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ArrayList<Character> getCharacters(String key) {
        return getChars(root, key);
    }

    // a foolish way to update the probability :\
    public void chooseCharacter(Character ch) {
        int pos = curChars.indexOf(ch);
        double x = curValue.get(pos);
        double arctanh = (0.5d * (Math.log((1d + x) / (1d - x)) / Math.log(Math.E))) * 100000d;
        double v = Math.tanh((arctanh + 1) / 100000d);
        System.out.println(pos + " " + arctanh);
        System.out.println(Math.tanh((arctanh + 1) / 100000d));
        try {
            curValue.set(pos, v);
            dic.seek(curNode.addressStart);
            dic.skipBytes(11 * pos + 3);
            // 3 bytes for each UTF-8 character
            dic.writeDouble(v);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNode(PinyinNode node, String key, String data) {
        if (key.length() == 0) {
            String info[] = data.split(",");
            node.addressStart = Long.parseLong(info[0]);
            node.length = Short.parseShort(info[1]);
            node.hasChild = true;
        } else {
            char cur = key.charAt(0);
            if (node.children[cur - 'a'] == null) {
                node.children[cur - 'a'] = new PinyinNode();
            }
            addNode(node.children[cur - 'a'], key.substring(1), data);
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Character> getChars(PinyinNode node, String key) {
        if (key.length() == 0) {
            if (node.hasChild) {
                try {
                    curNode = node;
                    dic.seek(node.addressStart);
                    curChars = new ArrayList<>();
                    curValue = new ArrayList<>();
                    for (int i = 0; i < node.length; i++) {
                        // 3 bytes for each UTF-8 character
                        byte singChar[] = new byte[3];
                        dic.read(singChar);
                        curChars.add(new String(singChar).charAt(0));
                        curValue.add(dic.readDouble());
                    }
                    ArrayList<Character> output = (ArrayList<Character>) curChars.clone();
                    output.sort(comparator);
                    return output;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                ArrayList<Character> output = new ArrayList<>();
                for (PinyinNode child : node.children) {
                    if (child != null) {
                        ArrayList<Character> candidate = getChars(child, key);
                        if (candidate != null) {

                        }
                    }
                }
                return output;
            }
        } else {
            char cur = key.charAt(0);
            if (node.children[cur - 'a'] == null) {
                throw new NoSuchElementException();
            }
            return getChars(node.children[cur - 'a'], key.substring(1));
        }
    }

    private class PinyinNode {
        long addressStart;
        short length;
        boolean hasChild;
        PinyinNode[] children;

        PinyinNode() {
            children = new PinyinNode[26];
            hasChild = false;
        }

    }

    private class ValueComparator implements Comparator<Character> {

        @Override
        public int compare(Character o1, Character o2) {
            int pos1 = curChars.indexOf(o1);
            int pos2 = curChars.indexOf(o2);
            if (curValue.get(pos1) > curValue.get(pos2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
