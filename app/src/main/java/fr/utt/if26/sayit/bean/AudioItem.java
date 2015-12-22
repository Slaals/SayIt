package fr.utt.if26.sayit.bean;

import java.io.File;

/**
 * Created by Jonathan on 21/12/2015.
 */
public class AudioItem {

    String id;
    File file;

    public AudioItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}
