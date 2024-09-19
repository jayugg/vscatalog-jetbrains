package com.github.sirnoname2705.vscatalog;

import com.github.sirnoname2705.vscatalog.model.JsonFile;
import com.github.sirnoname2705.vscatalog.model.SchemaFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DependencyResolver {

    private static Pattern pattern = Pattern.compile(com.github.sirnoname2705.vscatalog.settings.Util.refPattern);
    private static Set<String> alreadyDownloaded = new HashSet<>();

    private DependencyResolver() {

    }

    public static void resolveAndDownloadUrls(SchemaFile schema) {
        try {
            String fileContent = schema.getContent();
            var paths = getRefValues(fileContent);
            var baseUrl = schema.url;
            List<String> urls = new ArrayList<>(paths.size());
            for (String path : paths) {
                if (path.startsWith("#")) {
                    continue;
                }
                String url;
                url = resolveUrl(baseUrl, path);
                if (url.endsWith("#")) {
                    url = url.replace("#", "");
                }
                if (alreadyDownloaded.contains(url)) {
                    continue;
                }
                urls.add(url);
                JsonFile jsonFile = new JsonFile(url);
                jsonFile.downloadJson();
            }
            alreadyDownloaded.addAll(urls);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getRefValues(String jsonString) {
        List<String> results = new ArrayList<>();
        Matcher matcher = pattern.matcher(jsonString);
        while (matcher.find()) {
            var result = matcher.group(1);
            results.add(result);
        }
        return results;
    }

    public static String resolveUrl(String baseUrl, String relativePath) throws URISyntaxException {
        URI baseUri = new URI(baseUrl);
        URI resolvedUri = baseUri.resolve(relativePath);
        return resolvedUri.toString();
    }

    public static void clearDependencyCache() {
        alreadyDownloaded.clear();
    }

}
