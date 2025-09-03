package dev.floelly.ghostnetfishing.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nets")
public class Net {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private Long netId = UUID.randomUUID().getMostSignificantBits();

    @Column(nullable = false)
    private Double locationLat;

    @Column(nullable = false)
    private Double locationLong;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NetSize size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NetState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Net(Double locationLat, Double locationLong, NetSize size, NetState state) {
        this.locationLat = locationLat;
        this.locationLong = locationLong;
        this.size = size;
        this.state = state;
    }

    public Net(Double locationLat, Double locationLong, NetSize size) {
        this(locationLat, locationLong, size, NetState.REPORTED);
    }
}
