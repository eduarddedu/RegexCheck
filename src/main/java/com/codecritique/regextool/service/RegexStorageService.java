package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;

import java.util.List;

public interface RegexStorageService {
    void store(Regex regex);
    Regex get(String id);
    List<Regex> getAll();
    void update(Regex regex);
    void delete(String id);
    void init();
}
