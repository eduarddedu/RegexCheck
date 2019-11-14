package com.codecritique.regextool.service;

import com.codecritique.regextool.entity.Regex;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class XmlDbStorageService implements RegexStorageService {
    private XmlDb db;

    public XmlDbStorageService(StorageProperties properties) {
        this.db = new XmlDb(properties.getLocation());
    }

    @Override
    public void store(Regex regex) {
        this.db.store(regex);
    }

    @Override
    public Regex get(int id) {
        return this.db.get(id);
    }

    @Override
    public List<Regex> getAll() {
        return this.db.getAll();
    }

    @Override
    public void delete(int id) {
        this.db.delete(id);
    }

    @Override
    public void update(Regex regex) {
        this.db.update(regex);
    }

    @Override
    public void init() {
        this.db.init();
    }



}
