package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nets")
public class NetsController {

    @GetMapping("/new")
    public String getNewNetFormPage() {
        return "nets/new";
    }

    public String postNewNet(NewNetRequest newNetRequest) {
        return null;
    }
}
