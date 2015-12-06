package fr.utt.if26.sayit.bean;

public class ExpressionItem {

    private String itemName;
    private Country country;

    public ExpressionItem(String itemName, Country country) {
        this.itemName = itemName;
        this.country = country;
    }

    public String getItemName() {
        return itemName;
    }

    public Country getCountry() {
        return country;
    }
}