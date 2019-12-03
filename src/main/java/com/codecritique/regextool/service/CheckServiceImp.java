package com.codecritique.regextool.service;


import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CheckServiceImp implements CheckService {
    private final static String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public List<String> getMatchersAsList(String regex, String text) {
        Matcher m = Pattern.compile(regex).matcher(text);
        List<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    @Override
    public String getMatchersAsMultiLineText(String regex, String text) {
        StringBuilder sb = new StringBuilder();
        for (String match : getMatchersAsList(regex, text)) {
            sb.append(match);
            sb.append(LINE_SEPARATOR);
        }
        return sb.toString();
    }
}
