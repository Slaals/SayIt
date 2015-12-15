package fr.utt.if26.sayit.bean;

import android.support.annotation.Nullable;

import fr.utt.if26.sayit.R;

public enum Country {

    // TODO : Find a way to don't have an UI element into the bean package
    FRANCE("fr", R.drawable.flag_fr, R.string.langage_fr),
    SPAIN("es", R.drawable.flag_es, R.string.langage_sp),
    UNITED_STATES("us", R.drawable.flag_us, R.string.langage_us),
    GERMANY("de", R.drawable.flag_de, R.string.langage_de);

    private String isoCode;
    private int drawableResource;
    private int stringResource;

    Country(String isoCode, int drawableResource, int stringResource) {
        this.isoCode = isoCode;
        this.drawableResource = drawableResource;
        this.stringResource = stringResource;
    }

    public int getDrawableResource() {
        return drawableResource;
    }

    public int getStringResource() {
        return stringResource;
    }

    @Nullable
    public static Country getByIsoCode(String isoCode) {
        for (Country country : Country.values()) {
            if (country.getIsoCode().equals(isoCode)) {
                return country;
            }
        }
        return null;
    }

    public String getIsoCode() {
        return isoCode;
    }
}
