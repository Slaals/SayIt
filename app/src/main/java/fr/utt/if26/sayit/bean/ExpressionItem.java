package fr.utt.if26.sayit.bean;

import android.view.View;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ExpressionItem {

    private String itemName;
    private Country country;
    private int numberAudio;
    private String exprId;
    private String author;
    private String date;

    public ExpressionItem(String itemName, Country country, int numberAudio, String exprId,
                          String author, String date) {
        this.itemName = itemName;
        this.country = country;
        this.numberAudio = numberAudio;
        this.exprId = exprId;
        this.author = author;
        this.date = date;
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

    public String getAuthor() { return author; }

    public String getDate() {
        return this.date.split(" ")[0];
    }

    public String getTime() {
        return this.date.split(" ")[1];
    }
}