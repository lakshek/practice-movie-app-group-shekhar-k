package com.example.movies;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {
    private final MovieStore store;
    private final GeminiClient gemini;

    public MovieService(MovieStore store, GeminiClient gemini) {
        this.store = store;
        this.gemini = gemini;
    }

    public List<Movie> list() {
        return store.findAll();
    }

    public Movie add(String title, Double rating) {
        double r = (rating == null) ? 0.0 : rating;
        String desc = gemini.generateDescription(title, r);
        return store.save(title, rating, desc);
    }

    public boolean delete(Long id) {
        return store.delete(id);
    }
}