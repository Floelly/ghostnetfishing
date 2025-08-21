package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.service.INewNetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/nets")
public class NetsController {

    private final INewNetService newNetService;

    @GetMapping
    public String getNetsPage(Model model) {
        List<NetDTO> nets = newNetService.getAll();
        model.addAttribute("nets", nets);
        return "/nets";
    }

    @GetMapping("/new")
    public String getNewNetFormPage(Model model) {
        model.addAttribute("netSizes", NetSize.values());
        return "/nets/new";
    }

    @PostMapping("/new")
    public String postNewNet(@Valid @ModelAttribute NewNetRequest newNetRequest,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/nets/new";
        }
        newNetService.addNewNet(newNetRequest);
        return "redirect:/nets";
    }
}
