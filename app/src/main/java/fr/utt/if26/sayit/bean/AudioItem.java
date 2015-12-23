package fr.utt.if26.sayit.bean;

import java.io.File;

/**
 * Created by Jonathan on 21/12/2015.
 */
public class AudioItem {

    String id;
    String date;
    String author;

    File file;

    public AudioItem(String id, String date, String author) {
        this.id = id;
        this.date = date;
        this.author = author;
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

    public String getDate() {
        return date.split(" ")[0];
    }

    public String getTime() {
        return date.split(" ")[1];
    }

    public String getAuthor() {
        return author;
    }

}
