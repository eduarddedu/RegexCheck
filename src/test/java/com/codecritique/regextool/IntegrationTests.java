package com.codecritique.regextool;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldLoadAndCheckRegex() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        String regex = "\\w+";
        String text = "Foo\nBar\nBaz";
        params.put("regex", Collections.singletonList(regex));
        params.put("text", Collections.singletonList(text));
        ResponseEntity<String> response = testRestTemplate.postForEntity("/check", params, String.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    // TODO add more tests

}
