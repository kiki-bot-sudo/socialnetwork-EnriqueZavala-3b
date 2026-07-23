package com.enriquezavala.socialnetwork.domain;

import java.util.List;

public class User {
    private long id;
    private String username;
    private String email;
    private String password;
    private String name;
    private String biography;
    private String profilePicture;
    private List<Comment> comments;
    private List<Photograph> photographs;
    private List<LikePhotography> likes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Photograph> getPhotographs() {
        return photographs;
    }

    public void setPhotographs(List<Photograph> photographs) {
        this.photographs = photographs;
    }

    public List<LikePhotography> getLikes() {
        return likes;
    }

    public void setLikes(List<LikePhotography> likes) {
        this.likes = likes;
    }
}
