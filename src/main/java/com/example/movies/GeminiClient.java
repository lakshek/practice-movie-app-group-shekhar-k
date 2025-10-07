package com.example.movies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class GeminiClient {
    private static final String MODEL = "gemini-2.0-flash-001";
    private static final String URL =
            "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";

    private final RestTemplate rest = new RestTemplate();
    private final String apiKey;

    public GeminiClient(@Value("${GOOGLE_API_KEY:}") String apiKey) {
        this.apiKey = apiKey;
        System.out.println("Gemini key loaded? " + (apiKey != null && !apiKey.isBlank()));
    }

    public String generateDescription(String title, double rating) {
        if (apiKey == null || apiKey.isBlank()) {
            return "AI description unavailable (missing GOOGLE_API_KEY).";
        }

        String prompt = """
            Write a short, spoiler-free 1–2 sentence description (≤50 words).
            Title: "%s"
            Audience rating (0–10): %.1f
            Keep it neutral and informative.
            """.formatted(title, rating);

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt))
                ))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("x-goog-api-key", apiKey); // use header auth

        try {
            ResponseEntity<Map> resp = rest.postForEntity(URL, new HttpEntity<>(body, headers), Map.class);
            System.out.println("Gemini status: " + resp.getStatusCode());

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) return "AI description unavailable.";

            Object candObj = resp.getBody().get("candidates");
            if (!(candObj instanceof List<?> cands) || cands.isEmpty()) return "AI description unavailable.";
            Object contentObj = ((Map<?, ?>) cands.get(0)).get("content");
            if (!(contentObj instanceof Map<?, ?> contentMap)) return "AI description unavailable.";
            Object partsObj = contentMap.get("parts");
            if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) return "AI description unavailable.";
            Object text = ((Map<?, ?>) parts.get(0)).get("text");

            String out = text == null ? "" : text.toString().trim();
            return out.isBlank() ? "AI description unavailable." : out;
        } catch (HttpStatusCodeException e) {
            System.out.println("Gemini HTTP error: " + e.getStatusCode());
            System.out.println("Gemini error body: " + e.getResponseBodyAsString());
            return "AI description unavailable.";
        } catch (Exception e) {
            e.printStackTrace();
            return "AI description unavailable.";
        }
    }
}