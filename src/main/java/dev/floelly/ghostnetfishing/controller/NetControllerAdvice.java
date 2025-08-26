package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.ToastMessageResponse;
import dev.floelly.ghostnetfishing.dto.ToastType;
import dev.floelly.ghostnetfishing.model.IllegalNetStateChangeException;
import dev.floelly.ghostnetfishing.model.NetNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@ControllerAdvice
public class NetControllerAdvice {
    @ExceptionHandler({NetNotFoundException.class, IllegalNetStateChangeException.class})
    public String handleNetNotFound(RuntimeException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("toastMessages",
                List.of(new ToastMessageResponse(ex.getMessage(), ToastType.ERROR)));
        return "redirect:/nets";
    }
}
