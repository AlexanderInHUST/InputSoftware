package model;

import java.io.Serializable;

/**
 * Created by tangyifeng on 17/2/19.
 * Email: yifengtang_hust@outlook.com
 */
public class Word  {

    private String self;
    private double value;

    public Word(String self, double value) {
        this.self = self;
        this.value = value;
    }

    public void calculateValue(int counts) {
        int num = (int) (counts * value);
        value = (num + 1) / counts;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
