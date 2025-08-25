package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.Net;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetService implements INetService {
    private final List<NetDTO> nets = new ArrayList<>();

    private final NetRepository netRepository;

    public void addNewNet(NewNetRequest newNetRequest) {
        Net newNet = new Net(
                newNetRequest.getLocationLat(),
                newNetRequest.getLocationLong(),
                newNetRequest.getSize());
        netRepository.save(newNet);
    }

    public List<NetDTO> getAll() {
        return netRepository.findAll().stream()
                .map(net -> new NetDTO(
                        net.getNetId(),
                        net.getLocationLat(),
                        net.getLocationLong(),
                        net.getSize(),
                        net.getState())
                ).collect(Collectors.toList());
    }
}
