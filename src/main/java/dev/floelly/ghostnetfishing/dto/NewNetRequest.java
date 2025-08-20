package dev.floelly.ghostnetfishing.dto;

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
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLat;
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLong;
    private String size;
}
