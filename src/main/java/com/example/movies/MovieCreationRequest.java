package com.example.movies;

public class MovieCreationRequest {
private String title;
private Double rating;

public MovieCreationRequest() {}
public MovieCreationRequest(String title, Double rating) {
    this.title = title; this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}
