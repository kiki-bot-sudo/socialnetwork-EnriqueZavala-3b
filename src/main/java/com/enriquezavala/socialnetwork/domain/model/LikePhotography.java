package com.enriquezavala.socialnetwork.domain.model;

public class LikePhotography {
    private Long id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
