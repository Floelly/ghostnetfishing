package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.IllegalNetStateChangeException;
import dev.floelly.ghostnetfishing.model.Net;
import dev.floelly.ghostnetfishing.model.NetNotFoundException;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetService implements INetService {
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

    @Override
    @Transactional
    public void requestRecovery(Long id) {
        Net net = netRepository.findByNetId(id).orElseThrow(() -> new NetNotFoundException(id));
        if(!net.getState().equals(NetState.REPORTED)){
            throw new IllegalNetStateChangeException(id, net.getState(), NetState.RECOVERY_PENDING);
        }
        net.setState(NetState.RECOVERY_PENDING);
        netRepository.save(net);
    }

    @Override
    public List<NetDTO> getAllByState(NetState state) {
        return getAll().stream().filter(net -> net.getState().equals(state)).collect(Collectors.toList());
    }

    @Override
    public void markRecovered(Long id) {
        Net net = netRepository.findByNetId(id).orElseThrow(() -> new NetNotFoundException(id));
        if(net.getState().equals(NetState.RECOVERED) || net.getState().equals(NetState.LOST)){
            throw new IllegalNetStateChangeException(id, net.getState(), NetState.RECOVERED);
        }
        net.setState(NetState.RECOVERED);
        netRepository.save(net);
    }

    @Override
    public void markLost(Long id) {

    }
}
