package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;

import java.util.List;

public interface RegexStorageService {
    void store(Regex regex);
    List<Regex> getAll();
    void delete(String id);
    void init();
}
