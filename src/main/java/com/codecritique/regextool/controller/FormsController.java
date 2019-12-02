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
    public String showCheckedRegex() {
        return "index";
    }

    @PostMapping("/")
    private String loadCheckedRegex(@RequestParam String regex, @RequestParam String text, Model model) {
        model.addAttribute("regex", regex);
        model.addAttribute("text", text);
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

    @PostMapping("/store")
    public String storeRegex(@RequestParam String regex, @RequestParam String description, @RequestParam String text) {
        this.regexStorageService.store(new Regex(regex, description, text));
        return "redirect:/archive";
    }

    @GetMapping("/archive")
    public String showStoredEntities(Model model) {
        List<Regex> entities = regexStorageService.getAll();
        model.addAttribute("entities", entities);
        return "archive";
    }

    @PostMapping("/delete")
    private String deleteRegex(@RequestParam String id) {
        regexStorageService.delete(id);
        return "redirect:/archive";
    }

    @PostMapping("/edit")
    public String loadStoredRegexForEdit(@RequestParam String id, Model model) {
        Regex regex = this.regexStorageService.get(id);
        model.addAttribute("regex", regex.getValue());
        model.addAttribute("text", regex.getText());
        model.addAttribute("description", regex.getDescription());
        model.addAttribute("id", id);
        return "update";
    }

    @PostMapping("/update")
    public String updateRegex(@RequestParam String id, @RequestParam String regex,
                              @RequestParam String description, @RequestParam String text) {
        this.regexStorageService.update(new Regex(id, regex, description, text));
        return "redirect:/archive";
    }

    @PostMapping("/check")
    public String checkEditedRegex(@RequestParam String id, @RequestParam String regex, @RequestParam String description,
                                   @RequestParam String text, Model model) {
        model.addAttribute("regex", regex);
        model.addAttribute("text", text);
        model.addAttribute("description", description);
        model.addAttribute("id", id);
        try {
            String matchers = regexCheckService.getMatchersAsMultiLineText(regex, text);
            if (!matchers.isEmpty()) {
                model.addAttribute("matchers", matchers);
            }
        } catch (PatternSyntaxException e) {
            model.addAttribute("error", e);
        }
        return "update";
    }
}
