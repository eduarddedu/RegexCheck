package com.codecritique.regextool;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.StorageService;
import com.codecritique.regextool.service.StorageProperties;
import com.codecritique.regextool.service.XmlStorageService;
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

class XmlStorageServiceTests {

    private StorageService service;

    @BeforeEach
    void setupEmptyDatabase() throws IOException {
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
        service = new XmlStorageService(properties);
        service.init();
    }

    @Test
    void shouldGetAll() {
        List<Regex> expectedList = storeAll(new Regex(null, ".*", "F", "F"),
                new Regex(null, "[a-z]", "B", "B"),
                new Regex(null, "B.z", "B", "B"));
        List<Regex> actualList = service.getAll();
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            Regex expected = expectedList.get(i);
            Regex actual = actualList.get(i);
            assertEquals(expected.getValue(), actual.getValue());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
    }

    @Test
    void shouldGetAnExistingItem() {
        Regex expected = new Regex(null, "F", "F", "F\nF\nBaz");
        service.store(expected);
        Regex actual = service.getAll().get(0);
        assertEquals(expected.getValue(), actual.getValue());
        assertEquals(expected.getDescription(), actual.getDescription());
    }
    
    @Test
    void shouldDeleteAnExistingItem() {
        List<Regex> expectedList = storeAll(new Regex(null, "F", "F", "F"),
                new Regex(null, "[a-z]", "B", "B"),
                new Regex(null, "B.z", "B", "B"));
        String id = service.getAll().get(1).getId();
        service.delete(id);
        expectedList.remove(1);
        List<Regex> actualList = service.getAll();
        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            Regex expected = expectedList.get(i);
            Regex actual = actualList.get(i);
            assertEquals(expected.getValue(), actual.getValue());
            assertEquals(expected.getDescription(), actual.getDescription());
        }
    }

    @Test
    void shouldUpdateAnExistingItem() {
        List<Regex> expectedList = storeAll(new Regex(null, "F", "F", "F"),
                new Regex(null, "[a-z]", "B", "B"),
                new Regex(null, "B.z", "B", "B"));
        String id = service.getAll().get(1).getId();
        Regex regex = service.get(id);
        regex.setDescription("Baz");
        service.store(regex);
        assertEquals(expectedList.size(), service.getAll().size());
        assertEquals(regex, service.get(id));
    }

    private List<Regex> storeAll(Regex... entities) {
        List<Regex> items = new ArrayList<>();
        for (Regex entity : entities) {
            items.add(entity);
            service.store(entity);
        }
        return items;
    }
}
