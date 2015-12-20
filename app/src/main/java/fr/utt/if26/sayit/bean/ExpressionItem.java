package fr.utt.if26.sayit.bean;

public class ExpressionItem {

    private String itemName;
    private Country country;
    private int numberAudio;

    public ExpressionItem(String itemName, Country country, int numberAudio) {
        this.itemName = itemName;
        this.country = country;
        this.numberAudio = numberAudio;
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
}