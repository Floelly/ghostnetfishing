package dev.floelly.ghostnetfishing.dto;

import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.model.NetState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetDTO {
    private Long id;
    private Double locationLat;
    private Double locationLong;
    private NetSize size;
    private NetState state;
}
