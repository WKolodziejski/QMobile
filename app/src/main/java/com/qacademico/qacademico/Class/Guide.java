package com.qacademico.qacademico.Class;

import java.io.Serializable;

public class Guide implements Serializable {
    private final String title;
    private final String description;
    private final int image;
    private final int tint;

    public Guide(String title, String description, int image, int tint) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.tint = tint;
    }

    public int getTint() {
        return tint;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }
}
