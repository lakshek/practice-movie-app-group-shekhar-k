package com.example.movies;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MovieStore {
    private final Map<Long, Movie> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public List<Movie> findAll() {
        return new ArrayList<>(data.values());
    }

    public Movie save(String title, Double rating, String description) {
        long id = seq.getAndIncrement();
        Movie m = new Movie(id, title, rating, description);
        data.put(id, m);
        return m;
    }
}
