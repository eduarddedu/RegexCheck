package com.codecritique.regextool;

import com.codecritique.regextool.service.RegexCheckService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.regex.PatternSyntaxException;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.hamcrest.core.IsInstanceOf;


@AutoConfigureMockMvc
@SpringBootTest
class RegexCheckControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RegexCheckService regexCheckService;

    @Test
    void shouldLoadAndCheckRegex() throws Exception {
        String regex = ".*";
        String text = "input";
        String matchers = "input";
        String description = "matches anything";
        given(this.regexCheckService.getMatchersAsMultiLineText(regex, text)).willReturn(matchers);
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("regex", Collections.singletonList(regex));
        params.put("text", Collections.singletonList(text));
        params.put("description", Collections.singletonList(description));
        mvc.perform(post("/").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regex", regex))
                .andExpect(model().attribute("text", text))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("matchers", matchers));
    }

    @Test
    void shouldReturnErrorOnInvalidRegexInput() throws Exception {
        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String invalidRegex = "(.*";
        String text = "input";
        String description = "matches anything";
        params.put("regex", Collections.singletonList(invalidRegex));
        params.put("text", Collections.singletonList(text));
        params.put("description", Collections.singletonList(description));
        given(this.regexCheckService.getMatchersAsMultiLineText(invalidRegex, text)).willThrow(PatternSyntaxException.class);
        mvc.perform(post("/").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("regex", invalidRegex))
                .andExpect(model().attribute("text", text))
                .andExpect(model().attribute("description", description))
                .andExpect(model().attribute("error", IsInstanceOf.instanceOf(PatternSyntaxException.class)));
    }

}
