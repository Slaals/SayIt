package fr.utt.if26.sayit.bean;

import fr.utt.if26.sayit.R;

public enum Country {

    // TODO : Find a way to don't have an UI element into the bean package
    FRANCE("fr", R.drawable.flag_fr),
    UNITED_STATES("us", R.drawable.flag_us);

    private String isoCode;
    private int drawableResource;

    Country(String isoCode, int drawableResource) {
        this.isoCode = isoCode;
        this.drawableResource = drawableResource;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public String getIsoCode() {
        return isoCode;
    }
}