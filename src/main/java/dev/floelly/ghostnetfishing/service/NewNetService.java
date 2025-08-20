package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewNetService implements INewNetService {

    public void addNewNet(NewNetRequest newNetRequest) {

    }

    public List<NetDTO> getAll() {
        return List.of();
    }
}
