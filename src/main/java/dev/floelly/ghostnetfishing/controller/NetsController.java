package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.service.INewNetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/nets")
public class NetsController {

    private final INewNetService newNetService;

    @GetMapping
    public String getNetsPage(Model model) {
        return "/nets";
    }

    @GetMapping("/new")
    public String getNewNetFormPage() {
        return "/nets/new";
    }

    @PostMapping("/new")
    public String postNewNet(@ModelAttribute NewNetRequest newNetRequest) {
        newNetService.addNewNet(newNetRequest);
        return "redirect:/nets";
    }
}
