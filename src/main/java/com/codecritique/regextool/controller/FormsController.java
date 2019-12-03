package com.codecritique.regextool.controller;

import com.codecritique.regextool.entity.Regex;
import com.codecritique.regextool.service.CheckService;
import com.codecritique.regextool.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.regex.PatternSyntaxException;

@Controller
public class FormsController {

    @Autowired
    private CheckService checkService;

    @Autowired
    private StorageService storageService;

    @PostMapping("/check")
    private String checkRegex(@RequestParam String regex, @RequestParam String text,
                              @RequestParam(required = false) String id,
                              @RequestParam(required = false) String description,
                              Model model) {
        model.addAttribute("regex", regex);
        model.addAttribute("text", text);
        if (!StringUtils.isEmpty(id)) {
            model.addAttribute("id", id);
            model.addAttribute("description", description);
        }
        try {
            String matchers = checkService.getMatchersAsMultiLineText(regex, text);
            if (!matchers.isEmpty()) {
                model.addAttribute("matchers", matchers);
            }
        } catch (PatternSyntaxException e) {
            model.addAttribute("error", e);
        }
        return "index";
    }

    @PostMapping("/store")
    public String storeRegex(@RequestParam String id,
                             @RequestParam String regex, @RequestParam String description, @RequestParam String text) {
        this.storageService.store(new Regex(id, regex, description, text));
        return "redirect:/archive";
    }

    @GetMapping("/archive")
    public String showStoredEntities(Model model) {
        List<Regex> entities = storageService.getAll();
        model.addAttribute("entities", entities);
        return "archive";
    }

    @PostMapping("/delete")
    private String deleteRegex(@RequestParam String id) {
        storageService.delete(id);
        return "redirect:/archive";
    }

    @PostMapping("/edit")
    public String loadStoredRegexForEdit(@RequestParam String id, Model model) {
        Regex regex = this.storageService.get(id);
        return checkRegex(regex.getValue(), regex.getText(), id, regex.getDescription(), model);
    }
}
