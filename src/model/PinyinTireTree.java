package model;

import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.io.*;
import java.util.*;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class PinyinTireTree {

    public static final char NOT_CHOOSE = ' ';
    public static final String NOT_CHOOSE_S = " ";
    private static final double VALUE_DECAY_RATE = 0.99999;

    private PinyinNode root;
    private File config;
    private RandomAccessFile dic;
    private PinyinNode curNode;
    private ArrayList<Character> curChars;
    private ArrayList<Double> curValue;
    private ArrayList<Integer> curAddress;
    private ArrayList<String> curWords;
    private ArrayList<Double> curWordsValue;
    private ArrayList<Integer> curLengthOfWords;
    private boolean isClear;
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
        curAddress = new ArrayList<>();
        curWords = new ArrayList<>();
        curWordsValue = new ArrayList<>();
        curLengthOfWords = new ArrayList<>();
        isClear = true;
        return getChars(root, key);
    }

    // a foolish way to update the probability :\
    // input ' ' (blank) when you don't choose any word
    public TreeMap<String, Double> chooseCharacter(Character ch) {
        if (!ch.equals(NOT_CHOOSE)) {
            int pos = curChars.indexOf(ch);
            double x = curValue.get(pos);
            double arctanh = (0.5d * (Math.log((1d + x) / (1d - x)) / Math.log(Math.E))) * 1000d;
            double v;
            try {
                dic.seek(curNode.addressStart);
                for (int i = 0; i < curChars.size(); i++) {
                    if (i == pos) {
                        v = Math.tanh((arctanh + 1) / 1000d);
                    } else {
                        v = curValue.get(i) * VALUE_DECAY_RATE;
                    }
                    dic.skipBytes(3);
                    // 3 bytes for each UTF-8 character
                    dic.writeDouble(v);
                    dic.skipBytes(8);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return getWords(pos);
        }
        curNode = null;
        return null;
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
                    System.out.println(node.addressStart);
                    dic.seek(node.addressStart);
                    TreeMap<Character, Double> output = new TreeMap<>(comparator);
                    for (int i = 0; i < node.length; i++) {
                        // 3 bytes for each UTF-8 character
                        byte singChar[] = new byte[3];
                        dic.read(singChar);
                        Character c = new String(singChar).charAt(0);
                        Double d = dic.readDouble();
                        Integer a = dic.readInt();
                        Integer l = dic.readInt();
                        curChars.add(c);
                        curValue.add(d);
                        curAddress.add(a);
                        curLengthOfWords.add(l);
                        output.put(c, d);
                    }
                    if (!isClear) {
                        for (PinyinNode child : node.children) {
                            if (child != null) {
                                TreeMap<Character, Double> candidate = getChars(child, key);
                                if (candidate != null)
                                    output.putAll(candidate);
                            }
                        }
                    }
                    return output;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                TreeMap<Character, Double> output = new TreeMap<>(comparator);
                isClear = false;
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

    private TreeMap<String, Double> getWords(int pos) {
        int length = curLengthOfWords.get(pos);
        int address = curAddress.get(pos);
        try{
            RandomAccessFile wordDic = new RandomAccessFile("WordsDic.txt", "rw");
            TreeMap<String, Double> wordMap = new TreeMap<>(new WordsValueComparator());
            wordDic.seek(address);
            for (int i = 0; i < length; i++) {
                String word = wordDic.readUTF();
                Double value = wordDic.readDouble();
                curWords.add(word);
                curWordsValue.add(value);
                wordMap.put(word, value);
            }
            return wordMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    private class WordsValueComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int pos1 = curWords.indexOf(o1);
            int pos2 = curWords.indexOf(o2);
            if (pos1 == pos2) {
                return 0;
            } else if (curWordsValue.get(pos1) > curWordsValue.get(pos2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}
