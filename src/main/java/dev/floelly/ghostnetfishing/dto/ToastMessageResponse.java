package dev.floelly.ghostnetfishing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ToastMessageResponse {
    private String message;
    private ToastType type;

    public String getAdditionalToastClasses() {
        return switch (type) {
            case ERROR -> "bg-danger text-white";
            case WARNING -> "bg-warning text-white";
            case SUCCESS -> "bg-success text-white";
            default -> "bg-info";
        };
    }
}
