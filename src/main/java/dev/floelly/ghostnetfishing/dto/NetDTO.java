package dev.floelly.ghostnetfishing.dto;

import dev.floelly.ghostnetfishing.model.NetState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetDTO {
    private Long id;
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLat;
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    private Double locationLong;
    private String size;
    private NetState state;
}
