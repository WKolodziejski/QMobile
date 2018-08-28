package com.tinf.qacademico.Class;

import java.io.Serializable;

public class Shortcut implements Serializable {
    private final String title;
    private final int image;

    public Shortcut(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }
}
