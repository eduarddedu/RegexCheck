package com.codecritique.regextool.controller;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.RegexStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class RegexStorageController {
    @Autowired
    RegexStorageService regexStorageService;

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
