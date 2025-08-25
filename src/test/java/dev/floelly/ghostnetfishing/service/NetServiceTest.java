package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.Net;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NetServiceTest {
    @Mock
    private NetRepository netRepository; // Repository interface

    @InjectMocks
    private NetService netService; // Dein Service

    @Test
    void shouldSaveNewNetUsingRepository() {
        // given
        NewNetRequest request = new NewNetRequest(12.34, 56.78, NetSize.L);

        // when
        netService.addNewNet(request);

        // then
        ArgumentCaptor<Net> netCaptor = ArgumentCaptor.forClass(Net.class);
        verify(netRepository).save(netCaptor.capture());

        Net savedNet = netCaptor.getValue();
        assertThat(savedNet.getLocationLat())
                .as("Latitude of the saved net should match the request")
                .isEqualTo(request.getLocationLat());

        assertThat(savedNet.getLocationLong())
                .as("Longitude of the saved net should match the request")
                .isEqualTo(request.getLocationLong());

        assertThat(savedNet.getSize())
                .as("Size of the saved net should match the request")
                .isEqualTo(request.getSize());

        assertThat(savedNet.getState())
                .as("State of the saved net should be REPORTED by default")
                .isEqualTo(NetState.REPORTED);
    }

    @Test
    void getAll_shouldReturnAllSavedNetDTOs() {
        Net net1 = new Net( null, 123L,1.0, 2.0, NetSize.L, NetState.REPORTED);
        Net net2 = new Net(null, 456L, 3.0, 4.0, NetSize.M, NetState.RECOVERY_PENDING);
        when(netRepository.findAll()).thenReturn(List.of(net1, net2));

        List<NetDTO> result = netService.getAll();

        assertThat(result.size())
                .as("Service should return all persisted nets as DTOs")
                .isEqualTo(2);

        assertThat(result.get(0))
                .as("First DTO should match first entity")
                .extracting(NetDTO::getId, NetDTO::getLocationLat, NetDTO::getLocationLong, NetDTO::getSize, NetDTO::getState)
                .containsExactly(net1.getNetId(), net1.getLocationLat(), net1.getLocationLong(), net1.getSize(), net1.getState());

        assertThat(result.get(1))
                .as("Second DTO should match second entity")
                .extracting(NetDTO::getId, NetDTO::getLocationLat, NetDTO::getLocationLong, NetDTO::getSize, NetDTO::getState)
                .containsExactly(net2.getNetId(), net2.getLocationLat(), net2.getLocationLong(), net2.getSize(), net2.getState());
    }
}