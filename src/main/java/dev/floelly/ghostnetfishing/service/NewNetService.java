package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class NewNetService implements INewNetService {
    private List<NetDTO> nets = new ArrayList<>();

    public void addNewNet(NewNetRequest newNetRequest) {

        NetDTO newNetDTO = new NetDTO(ThreadLocalRandom.current().nextLong(), newNetRequest.getLocationLat(), newNetRequest.getLocationLong(), newNetRequest.getSize(), NetState.REPORTED);
        nets.add(newNetDTO);
    }

    public List<NetDTO> getAll() {
        return nets;
    }
}
