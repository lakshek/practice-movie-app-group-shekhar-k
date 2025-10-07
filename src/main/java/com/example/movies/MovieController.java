package com.example.movies;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @GetMapping
    public List<Movie> list() {
        return service.list();
    }

    @PostMapping
    public Movie create(@RequestBody MovieCreationRequest req) {
        return service.add(req.getTitle(), req.getRating());
    }
}