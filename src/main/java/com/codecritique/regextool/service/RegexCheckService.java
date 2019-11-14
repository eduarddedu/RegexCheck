package com.codecritique.regextool.service;

import java.util.List;

public interface RegexCheckService {
    List<String> getMatchersAsList(String regex, String text);
    String getMatchersAsMultiLineText(String regex, String text);
}

