package dev.floelly.ghostnetfishing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewNetRequest {
    private String locationLat;
    private String locationLong;
    private String size;
}
