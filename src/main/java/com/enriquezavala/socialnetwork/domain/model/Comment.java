package com.enriquezavala.socialnetwork.domain.model;

public class Comment {
    private Long id;
    private String content;
    private User user;
    private Photograph photograph;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Photograph getPhotograph() {
        return photograph;
    }

    public void setPhotograph(Photograph photograph) {
        this.photograph = photograph;
    }
}
