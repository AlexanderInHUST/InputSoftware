package model;

import java.io.*;
import java.util.*;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class PinyinTireTree {

    public static final char NOT_CHOOSE = ' ';

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

    public TreeMap<Character, Double> getCharacters(String key) {
        curChars = new ArrayList<>();
        curValue = new ArrayList<>();
        return getChars(root, key);
    }

    // a foolish way to update the probability :\
    // input ' ' (blank) when you don't choose any word
    public void chooseCharacter(Character ch) {
        if(!ch.equals(NOT_CHOOSE)) {
            int pos = curChars.indexOf(ch);
            double x = curValue.get(pos);
            double arctanh = (0.5d * (Math.log((1d + x) / (1d - x)) / Math.log(Math.E))) * 1000d;
            double v = Math.tanh((arctanh + 1) / 1000d);
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
        curNode = null;
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
    private TreeMap<Character, Double> getChars(PinyinNode node, String key) {
        if (key.length() == 0) {
            if (node.hasChild) {
                try {
                    if (curNode == null)
                        curNode = node;
                    dic.seek(node.addressStart);
                    TreeMap<Character, Double> output = new TreeMap<>(comparator);
                    for (int i = 0; i < node.length; i++) {
                        // 3 bytes for each UTF-8 character
                        byte singChar[] = new byte[3];
                        dic.read(singChar);
                        Character c = new String(singChar).charAt(0);
                        Double d = dic.readDouble();
                        curChars.add(c);
                        curValue.add(d);
                        output.put(c, d);
                    }
                    return output;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                TreeMap<Character, Double> output = new TreeMap<>(comparator);
                for (PinyinNode child : node.children) {
                    if (child != null) {
                        TreeMap<Character, Double> candidate = getChars(child, key);
                        if (candidate != null) {
                            output.putAll(candidate);
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
            if (pos1 == pos2) {
                return 0;
            } else if (curValue.get(pos1) > curValue.get(pos2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
