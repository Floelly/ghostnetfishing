package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.*;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
                .as("Latitude of the saved net should match the size of request object")
                .isEqualTo(request.getLocationLat());

        assertThat(savedNet.getLocationLong())
                .as("Longitude of the saved net should match the size of request object")
                .isEqualTo(request.getLocationLong());

        assertThat(savedNet.getSize())
                .as("Size of the saved net should match the size of request object")
                .isEqualTo(request.getSize());

        assertThat(savedNet.getState())
                .as("net state should be '"+NetState.REPORTED+"' by default.")
                .isEqualTo(NetState.REPORTED);
    }

    @Test
    void shouldReturnAllSavedNetDTOs_onGetAll() {
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

    @Test
    void shouldUpdateRepository_whenNetStateIsReported_onRequestRecovery() {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, NetState.REPORTED);

        // when
        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));
        netService.requestRecovery(netId);

        // then
        verify(netRepository).findByNetId(eq(netId));

        ArgumentCaptor<Net> netCaptor = ArgumentCaptor.forClass(Net.class);
        verify(netRepository).save(netCaptor.capture());

        Net updatedNet = netCaptor.getValue();
        assertThat(updatedNet.getNetId())
                .as("net id shouldn't change")
                .isEqualTo(databaseNet.getNetId());

        assertThat(updatedNet.getLocationLat())
                .as("net latitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLat());

        assertThat(updatedNet.getLocationLong())
                .as("net longitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLong());

        assertThat(updatedNet.getSize())
                .as("net size shouldn't change")
                .isEqualTo(databaseNet.getSize());

        assertThat(updatedNet.getState())
                .as("should update net state.")
                .isEqualTo(NetState.RECOVERY_PENDING);
    }

    @Test
    void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnRequestRecovery() {
        // given
        Long netId = 0L;

        //when
        when(netRepository.findByNetId(any())).thenReturn(Optional.empty());
        assertThrows(NetNotFoundException.class, () -> netService.requestRecovery(netId));

        // then
        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED", "RECOVERY_PENDING"})
    void shouldThrowIllegalNetStateChange_whenInvalidState_onRequestRecovery(NetState state) {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, state);

        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));

        // when / then
        assertThrows(IllegalNetStateChangeException.class, () -> netService.requestRecovery(netId));

        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = NetState.class, names = {"RECOVERY_PENDING", "REPORTED"})
    void shouldUpdateRepository_whenNetStateIsValid_onMarkRecovered(NetState state) {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, state);

        // when
        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));
        netService.markRecovered(netId);

        // then
        verify(netRepository).findByNetId(eq(netId));

        ArgumentCaptor<Net> netCaptor = ArgumentCaptor.forClass(Net.class);
        verify(netRepository).save(netCaptor.capture());

        Net updatedNet = netCaptor.getValue();
        assertThat(updatedNet.getNetId())
                .as("net id shouldn't change")
                .isEqualTo(databaseNet.getNetId());

        assertThat(updatedNet.getLocationLat())
                .as("net latitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLat());

        assertThat(updatedNet.getLocationLong())
                .as("net longitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLong());

        assertThat(updatedNet.getSize())
                .as("net size shouldn't change")
                .isEqualTo(databaseNet.getSize());

        assertThat(updatedNet.getState())
                .as("should update net state.")
                .isEqualTo(NetState.RECOVERED);
    }

    @Test
    void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnMarkRecovered() {
        // given
        Long netId = 0L;

        //when
        when(netRepository.findByNetId(any())).thenReturn(Optional.empty());
        assertThrows(NetNotFoundException.class, () -> netService.markRecovered(netId));

        // then
        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED"})
    void shouldThrowIllegalNetStateChange_whenInvalidState_onMarkRecovered(NetState state) {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, state);

        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));

        // when / then
        assertThrows(IllegalNetStateChangeException.class, () -> netService.markRecovered(netId));

        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = NetState.class, names = {"RECOVERY_PENDING", "REPORTED"})
    void shouldUpdateRepository_whenNetStateIsValid_onMarkLost(NetState state) {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, state);

        // when
        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));
        netService.markLost(netId);

        // then
        verify(netRepository).findByNetId(eq(netId));

        ArgumentCaptor<Net> netCaptor = ArgumentCaptor.forClass(Net.class);
        verify(netRepository).save(netCaptor.capture());

        Net updatedNet = netCaptor.getValue();
        assertThat(updatedNet.getNetId())
                .as("net id shouldn't change")
                .isEqualTo(databaseNet.getNetId());

        assertThat(updatedNet.getLocationLat())
                .as("net latitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLat());

        assertThat(updatedNet.getLocationLong())
                .as("net longitude shouldn't change")
                .isEqualTo(databaseNet.getLocationLong());

        assertThat(updatedNet.getSize())
                .as("net size shouldn't change")
                .isEqualTo(databaseNet.getSize());

        assertThat(updatedNet.getState())
                .as("should update net state.")
                .isEqualTo(NetState.LOST);
    }

    @Test
    void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnMarkLost() {
        // given
        Long netId = 0L;

        //when
        when(netRepository.findByNetId(any())).thenReturn(Optional.empty());
        assertThrows(NetNotFoundException.class, () -> netService.markLost(netId));

        // then
        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    @ParameterizedTest
    @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED"})
    void shouldThrowIllegalNetStateChange_whenInvalidState_onMarkLost(NetState state) {
        // given
        Long netId = UUID.randomUUID().getMostSignificantBits();
        Net databaseNet = createDefaultNet(netId, state);

        when(netRepository.findByNetId(eq(netId))).thenReturn(Optional.of(databaseNet));

        // when / then
        assertThrows(IllegalNetStateChangeException.class, () -> netService.markLost(netId));

        verify(netRepository).findByNetId(eq(netId));
        verify(netRepository, never()).save(any());
    }

    private static @NotNull Net createDefaultNet(Long netId, NetState netState) {
        return new Net(null, netId, 1.0, 2.0, NetSize.L, netState);
    }
}