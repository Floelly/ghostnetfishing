package dev.floelly.ghostnetfishing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nets")
public class NetsController {

    @GetMapping("/new")
    public String getNewNetForm() {
        return "nets/new";
    }
}
