package com.codecritique.regextool;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.RegexCheckService;
import com.codecritique.regextool.service.RegexStorageService;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
class FormsControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RegexCheckService regexCheckService;

    @MockBean
    private RegexStorageService regexStorageService;

    @Test
    void shouldLoadAndCheckRegex() throws Exception {
        String regex = ".*";
        String text = "input";
        String matchers = "input";
        given(this.regexCheckService.getMatchersAsMultiLineText(regex, text)).willReturn(matchers);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("regex", Collections.singletonList(regex));
        params.put("text", Collections.singletonList(text));
        mvc.perform(post("/").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regex", regex))
                .andExpect(model().attribute("text", text))
                .andExpect(model().attribute("matchers", matchers));
    }

    @Test
    void shouldReturnErrorOnInvalidRegexInput() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String invalidRegexInput = "(.*";
        String textInput = "input";
        params.put("text", Collections.singletonList(textInput));
        params.put("regex", Collections.singletonList(invalidRegexInput));
        given(this.regexCheckService.getMatchersAsMultiLineText(invalidRegexInput, textInput)).willThrow(PatternSyntaxException.class);
        mvc.perform(post("/").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regex", invalidRegexInput))
                .andExpect(model().attribute("text", textInput))
                .andExpect(model().attribute("error", IsInstanceOf.instanceOf(PatternSyntaxException.class)));
    }

    @Test
    void shouldLoadRegexEntityForEdit() throws Exception {
        Regex entity = new Regex("1", ".*", "input text", "input description");
        given(this.regexStorageService.get(entity.getId())).willReturn(entity);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("id", Collections.singletonList(entity.getId()));
        mvc.perform(post("/edit").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("id", entity.getId()))
                .andExpect(model().attribute("regex", entity.getValue()))
                .andExpect(model().attribute("text", entity.getText()))
                .andExpect(model().attribute("description", entity.getDescription()));
    }

    @Test
    void shouldCheckAndReloadEditedRegex() throws Exception {
        String regex = ".*";
        String id = "1";
        String text = "text";
        String description = "description";
        given(this.regexCheckService.getMatchersAsMultiLineText(regex, text)).willReturn(text);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("id", Collections.singletonList(id));
        params.put("regex", Collections.singletonList(regex));
        params.put("description", Collections.singletonList(description));
        params.put("text", Collections.singletonList(text));
        mvc.perform(post("/check").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("id", id))
                .andExpect(model().attribute("regex", regex))
                .andExpect(model().attribute("text", text))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("matchers", text));
    }

    @Test
    void shouldRedirectToArchivePageAfterStoringUpdatedRegex() throws Exception {
        Regex entity = new Regex("1", ".*", "input text", "input description");
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("id", Collections.singletonList(entity.getId()));
        params.put("regex", Collections.singletonList(entity.getValue()));
        params.put("description", Collections.singletonList(entity.getDescription()));
        params.put("text", Collections.singletonList(entity.getText()));
        List<Regex> entities = Collections.singletonList(entity);
        given(regexStorageService.getAll()).willReturn(entities);
        mvc.perform(post("/update").params(params))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/archive"));
    }

    @Test
    void shouldLoadRegexEntities() throws Exception {
        List<Regex> entities = Arrays.asList(
                new Regex(".*", "matches any string", ""),
                new Regex("\\w+", "a word", ""));
        given(regexStorageService.getAll()).willReturn(entities);
        mvc.perform(get("/archive")).andExpect(status().isOk()).andExpect(model().attribute("entities", entities));
    }

}
