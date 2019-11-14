package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;

import java.util.List;

public interface RegexStorageService {
    void store(Regex regex);
    Regex get(int id);
    List<Regex> getAll();
    void delete(int id);
    void update(Regex regex);
    void init();
}
