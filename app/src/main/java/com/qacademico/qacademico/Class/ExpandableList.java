package com.qacademico.qacademico.Class;

import java.io.Serializable;
import java.util.List;

public class ExpandableList implements Serializable {
    private final String title;
    private final List<?> list;
    transient private boolean isExpanded;
    transient private boolean anim;

    public ExpandableList(String title, List<?> list) {
        this.title = title;
        this.list = list;
        this.anim = false;
        this.isExpanded = false;
    }

    public String getTitle() {
        return title;
    }

    public List<?> getList() {
        return list;
    }

    public boolean getExpanded(){
        return isExpanded;
    }

    public void setExpanded(boolean expanded){
        isExpanded = expanded;
    }

    public boolean getAnim(){
        return anim;
    }

    public void setAnim(boolean anim){
        this.anim = anim;
    }
}
