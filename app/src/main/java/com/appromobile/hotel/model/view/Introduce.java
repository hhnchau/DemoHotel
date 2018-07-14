package com.appromobile.hotel.model.view;

/**
 * Created by appro on 04/05/2018.
 */

public class Introduce {
    private String title;
    private String content;
    private int introTop;
    private int introImage;

    public Introduce(String title, String content, int introTop, int introImage) {
        this.title = title;
        this.content = content;
        this.introTop = introTop;
        this.introImage = introImage;
    }

    public String getTitle() {
        return title;
    }

    public Introduce setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Introduce setContent(String content) {
        this.content = content;
        return this;
    }

    public int getIntroTop() {
        return introTop;
    }

    public Introduce setIntroTop(int introTop) {
        this.introTop = introTop;
        return this;
    }

    public int getIntroImage() {
        return introImage;
    }

    public Introduce setIntroImage(int introImage) {
        this.introImage = introImage;
        return this;
    }
}
