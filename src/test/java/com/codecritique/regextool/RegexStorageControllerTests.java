package com.codecritique.regextool;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.RegexStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class RegexStorageControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RegexStorageService regexStorageService;

    @Test
    void shouldLoadRegexEntities() throws Exception {
        List<Regex> entities = Arrays.asList(
                new Regex(1, ".*", "matches any string", ""),
                new Regex(2, "\\w+", "a word", ""));
        given(regexStorageService.getAll()).willReturn(entities);
        mvc.perform(get("/archive")).andExpect(status().isOk()).andExpect(model().attribute("entities", entities));
    }
}
