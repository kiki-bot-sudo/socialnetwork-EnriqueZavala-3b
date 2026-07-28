package com.enriquezavala.socialnetwork.domain.model;

import java.util.List;

public class Photograph {
    private long id;
    private String title;
    private String description;
    private String imageUrl;
    private User user;
    private List<Comment> comments;
    private List<LikePhotography> Likes;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<LikePhotography> getLikes() {
        return Likes;
    }

    public void setLikes(List<LikePhotography> likes) {
        Likes = likes;
    }
}
