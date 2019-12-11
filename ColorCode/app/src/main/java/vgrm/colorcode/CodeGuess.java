package vgrm.colorcode;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by vgrmm on 2019-10-14.
 */

public class CodeGuess implements Serializable {
    private int index;
    private int correctPos;
    private int correctColor;
    private int[] code;
    private ImageView[] codeColored;

    public CodeGuess() {
    }

    public CodeGuess(int index, int correctPos, int correctColor, int[] code, ImageView[] codeColored) {
        this.index = index;
        this.correctPos = correctPos;
        this.correctColor = correctColor;
        this.code = code;
        this.codeColored = codeColored;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCorrectPos() {
        return correctPos;
    }

    public void setCorrectPos(int correctPos) {
        this.correctPos = correctPos;
    }

    public int getCorrectColor() {
        return correctColor;
    }

    public void setCorrectColor(int correctColor) {
        this.correctColor = correctColor;
    }

    public int[] getCode() {
        return code;
    }

    public void setCode(int[] code) {
        this.code = code;
    }

    public ImageView[] getCodeColored() { return codeColored; }

    public void setCodeColored(ImageView[] codeColored) { this.codeColored = codeColored; }

    public int getLength() {return 1;}
}
