package fr.utt.if26.sayit.bean;

import android.view.View;

public class ExpressionItem {

    private String itemName;
    private Country country;
    private int numberAudio;
    private String exprId;

    public ExpressionItem(String itemName, Country country, int numberAudio, String exprId) {
        this.itemName = itemName;
        this.country = country;
        this.numberAudio = numberAudio;
        this.exprId = exprId;
    }

    public String getItemName() {
        return itemName;
    }

    public Country getCountry() {
        return country;
    }

    public int getNumberAudio() {
        return numberAudio;
    }

    public String getExprId() {
        return exprId;
    }
}