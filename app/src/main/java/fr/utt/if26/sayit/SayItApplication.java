package fr.utt.if26.sayit;

import android.app.Application;

public class SayItApplication extends Application {

    private static SayItApplication instance;

    public static SayItApplication getInstance() {
        if (instance == null) {
            instance = new SayItApplication();
        }
        return instance;
    }

    /*
    The singleton pattern specify that the visibility of the constructor
    must be private, but in Android, the unique Application class constructor
    has to be public (if not, an fatal error occurs)
    */
    public SayItApplication() {
    }
}
