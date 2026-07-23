package com.enriquezavala.socialnetwork.domain;

public class LikePhotography {
    private long id;
    private User user;
    private Photograph photograph;

    public Photograph getPhotograph() {
        return photograph;
    }

    public void setPhotograph(Photograph photograph) {
        this.photograph = photograph;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
