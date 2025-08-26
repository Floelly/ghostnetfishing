package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.ToastMessageResponse;
import dev.floelly.ghostnetfishing.dto.ToastType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, RedirectAttributes redirectAttributes) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Required type: '%s'", ex.getValue(), ex.getName(), ex.getRequiredType());
        redirectAttributes.addFlashAttribute("toastMessages",
                List.of(new ToastMessageResponse(message, ToastType.ERROR)));
        return "redirect:/nets";
    }
}
