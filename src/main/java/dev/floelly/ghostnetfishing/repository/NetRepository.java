package dev.floelly.ghostnetfishing.repository;

import dev.floelly.ghostnetfishing.model.Net;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NetRepository extends JpaRepository<Net, Long> {
    Optional<Net> findByNetId(Long eq);
}
