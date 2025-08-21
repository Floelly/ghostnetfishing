package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.service.INetService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyEditorSupport;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/nets")
public class NetsController {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(NetSize.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    setValue(NetSize.valueOf(text));
                } catch (IllegalArgumentException e) {
                    setValue(null);
                }
            }
        });
    }

    private final INetService netService;

    @GetMapping
    public String getNetsPage(Model model) {
        List<NetDTO> nets = netService.getAll();
        model.addAttribute("nets", nets);
        return "/nets";
    }

    @GetMapping("/new")
    public String getNewNetFormPage(Model model) {
        model.addAttribute("newNetRequest", new NewNetRequest());
        model.addAttribute("netSizes", NetSize.values());
        return "/nets/new";
    }

    @PostMapping("/new")
    public String postNewNet(@Valid @ModelAttribute NewNetRequest newNetRequest,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("netSizes", NetSize.values());
            return "/nets/new";
        }
        netService.addNewNet(newNetRequest);
        return "redirect:/nets";
    }
}
