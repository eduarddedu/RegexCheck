package com.codecritique.regextool;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.RegexStorageService;
import com.codecritique.regextool.service.XmlDbStorageService;
import com.codecritique.regextool.service.StorageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class XmlDbStorageServiceTests {

    private RegexStorageService service;

    @BeforeEach
    void createEmptyDatabase() throws IOException {
        String location = "out/test/db/data.xml";
        Path dbPath = Paths.get(new FileSystemResource(location).getPath());
        if (!dbPath.toFile().exists()) {
            Files.createDirectories(dbPath.getParent());
            Files.createFile(dbPath);
        }
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><storage></storage>";
        Files.copy(new ByteArrayInputStream(xml.getBytes()), dbPath, StandardCopyOption.REPLACE_EXISTING);
        StorageProperties properties = new StorageProperties();
        properties.setLocation(location);
        service = new XmlDbStorageService(properties);
        service.init();
    }

    @Test
    void shouldGetAll() {
        List<Regex> expected = storeAll(new Regex(1, ".*", "F", "F"),
                new Regex(2, "[a-z]", "B", "B"),
                new Regex(3, "B.z", "B", "B"));
        List<Regex> actual = service.getAll();
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void shouldGetAnExistingItem() {
        Regex regex = new Regex(1, "F", "F", "F");
        service.store(regex);
        assertEquals(regex, service.get(1));
    }

    @Test
    void shouldDeleteAnExistingItem() {
        List<Regex> expected = storeAll(new Regex(1, "F", "F", "F"),
                new Regex(2, "[a-z]", "B", "B"),
                new Regex(3, "B.z", "B", "B"));
        service.delete(2);
        expected.remove(1);
        List<Regex> actual = service.getAll();
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void shouldUpdateAnExistingItem() {
        Regex regex = new Regex("F", "Foo", "F");
        service.store(regex);
        regex.setDescription("Bar");
        service.update(regex);
        assertEquals(service.getAll().size(), 1);
        assertEquals(service.get(1), regex);
    }

    private List<Regex> storeAll(Regex ...entities) {
        List<Regex> items = new ArrayList<>();
        for (Regex entity : entities) {
            items.add(entity);
            service.store(entity);
        }
        return items;
    }
}
