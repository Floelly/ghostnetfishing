package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;

import java.util.List;

public interface INewNetService {
    void addNewNet(NewNetRequest newNetRequest);

    List<NetDTO> getAll();
}
