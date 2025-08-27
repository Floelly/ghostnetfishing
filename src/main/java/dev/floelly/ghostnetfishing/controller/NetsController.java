package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.dto.ToastMessageResponse;
import dev.floelly.ghostnetfishing.dto.ToastType;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.service.INetService;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        binder.registerCustomEditor(NetState.class,  new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    setValue(NetState.valueOf(text));
                } catch (IllegalArgumentException e) {
                    setValue(null);
                }
            }
        });
    }

    private final INetService netService;

    @GetMapping
    public String getNetsPage(Model model, @RequestParam @Valid @Nullable NetState state) {
        List<NetDTO> nets = state == null ? netService.getAll() : netService.getAllByState(state);
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
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("netSizes", NetSize.values());
            return "/nets/new";
        }
        netService.addNewNet(newNetRequest);
        List<ToastMessageResponse> toastMessages = List.of(new ToastMessageResponse("New net added successfully", ToastType.SUCCESS));
        redirectAttributes.addFlashAttribute("toastMessages", toastMessages);
        return "redirect:/nets";
    }

    @PostMapping("/{id}/request-recovery")
    public String requestNetRecovery(@PathVariable long id) {
        netService.requestRecovery(id);
        return "redirect:/nets";
    }

    public String markRecovered(Long netId) {
        return null;
    }
}
