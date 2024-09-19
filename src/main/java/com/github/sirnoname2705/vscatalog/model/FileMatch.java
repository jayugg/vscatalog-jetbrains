package com.github.sirnoname2705.vscatalog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class FileMatch {
    private Pattern regexPattern;

    public FileMatch(String pattern) {
        this.regexPattern = Pattern.compile(TranslatePattern(pattern));
    }

    private static String TranslatePattern(String pattern) {
        String result = pattern.replace("**", ".*");
        result = result.replace("/", "");
        return result;
    }

    public static List<FileMatch> fromCollection(Collection<String> patterns) {
        List<FileMatch> result = new ArrayList<>();
        for (String pattern : patterns) {
            result.add(new FileMatch(pattern));
        }
        return result;
    }

    public boolean isMatch(String path) {
        var match = regexPattern.matcher(path).matches();
        return match;
    }
}