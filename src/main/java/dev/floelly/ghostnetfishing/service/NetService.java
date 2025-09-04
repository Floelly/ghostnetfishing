package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.*;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NetService implements INetService {
    private final NetRepository netRepository;
    private final IUserService userService;

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
                        net.getState(),
                        net.getUser() != null? net.getUser().getUsername() : null)
                ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void requestRecovery(Long id, String username) {
        Net net = netRepository.findByNetId(id).orElseThrow(() -> new NetNotFoundException(id));
        if(!net.getState().equals(NetState.REPORTED)){
            throw new IllegalNetStateChangeException(id, net.getState(), NetState.RECOVERY_PENDING);
        }
        if(net.getUser() != null){
            throw new IllegalNetStateException("This net should not have state " + NetState.REPORTED + " and an owner");
        }
        User user = userService.findByUsername(username);
        if(Objects.isNull(user.getPhone()) || user.getPhone().isBlank()){
            throw new AccessDeniedException("You need to register with a phone number, to use this function");
        }
        net.setState(NetState.RECOVERY_PENDING);
        net.setUser(user);
        netRepository.save(net);
    }

    @Override
    public List<NetDTO> getAllByState(NetState state) {
        return getAll().stream().filter(net -> net.getState().equals(state)).collect(Collectors.toList());
    }

    @Override
    public void markRecovered(Long id, String username) {
        Net net = netRepository.findByNetId(id).orElseThrow(() -> new NetNotFoundException(id));
        if(net.getState().equals(NetState.RECOVERED) || net.getState().equals(NetState.LOST)){
            throw new IllegalNetStateChangeException(id, net.getState(), NetState.RECOVERED);
        }
        if(net.getState().equals(NetState.RECOVERY_PENDING) && net.getUser() == null){
            throw new IllegalNetStateException("This net should not have state " + NetState.RECOVERY_PENDING + " and no owner");
        }
        User user = userService.findByUsername(username);
        if(Objects.isNull(user.getPhone()) || user.getPhone().isBlank()){
            throw new AccessDeniedException("You need to register with a phone number, to use this function");
        }
        if(net.getState().equals(NetState.RECOVERY_PENDING) && !net.getUser().getId().equals(user.getId())){
            throw new AccessDeniedException("You did not request to recover this net this net.");
        }
        net.setState(NetState.RECOVERED);
        net.setUser(null);
        netRepository.save(net);
    }

    @Override
    public void markLost(Long id) {
        Net net = netRepository.findByNetId(id).orElseThrow(() -> new NetNotFoundException(id));
        if(!net.getState().equals(NetState.RECOVERY_PENDING) && !net.getState().equals(NetState.REPORTED)){
            throw new IllegalNetStateChangeException(id, net.getState(), NetState.LOST);
        }
        if(net.getState().equals(NetState.RECOVERY_PENDING)){
            net.setUser(null);
        }
        net.setState(NetState.LOST);
        netRepository.save(net);
    }
}
