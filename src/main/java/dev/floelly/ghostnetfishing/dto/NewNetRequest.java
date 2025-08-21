package dev.floelly.ghostnetfishing.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewNetRequest {

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLat;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLong;

    @NotNull
    @Pattern(regexp = "S|M|L|XL", message = "Size must be one of S, M, L, XL")
    private String size;
}
