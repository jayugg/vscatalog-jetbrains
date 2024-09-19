package com.github.sirnoname2705.vscatalog;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.sirnoname2705.vscatalog.settings.Util.getCatalogUrl;

public final class JsonFetcher {
    public static String fetchJsonFromUrl(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return response.body();
    }

    public static String fetchJsonFromUrl() {
        return fetchJsonFromUrl(getCatalogUrl());
    }
}
