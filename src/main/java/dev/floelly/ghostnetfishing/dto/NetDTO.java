package dev.floelly.ghostnetfishing.dto;

import dev.floelly.ghostnetfishing.model.NetState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetDTO {
    private Long id;
    private Double locationLong;
    private Double locationLat;
    private String size;
    private NetState state;
}
