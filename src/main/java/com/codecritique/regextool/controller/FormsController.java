package com.codecritique.regextool.controller;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.RegexCheckService;
import com.codecritique.regextool.service.RegexStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.PatternSyntaxException;

@Controller
public class FormsController {

    @Autowired
    private RegexCheckService regexCheckService;

    @Autowired
    private RegexStorageService regexStorageService;

    @GetMapping("/")
    public String loadEmptyForm() {
        return "index";
    }

    @PostMapping("/")
    private String checkRegex(@RequestParam String regex, @RequestParam String text, @RequestParam String description, Model model) {
        model.addAttribute("regex", regex);
        model.addAttribute("text", text);
        model.addAttribute("description", description);
        try {
            String matchers = regexCheckService.getMatchersAsMultiLineText(regex, text);
            if (!matchers.isEmpty()) {
                model.addAttribute("matchers", matchers);
            }
        } catch (PatternSyntaxException e) {
            model.addAttribute("error", e);
        }
        return "index";
    }

    @PostMapping("/edit")
    private String loadRegexEntity(@RequestParam int id, Model model) {
        Regex entity = regexStorageService.get(id);
        return checkRegex(entity.getValue(), entity.getText(), entity.getDescription(), model);
    }

    @GetMapping("/archive")
    public String loadPage(Model model) {
        List<Regex> entities = regexStorageService.getAll();
        model.addAttribute("entities", entities);
        return "archive";
    }

    @PostMapping("/archive")
    public String storeRegex(@RequestParam String regex, @RequestParam String text, @RequestParam String description) {
        this.regexStorageService.store(new Regex(regex, description, text));
        return "redirect:/archive";
    }
}
