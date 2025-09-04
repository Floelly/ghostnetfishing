package dev.floelly.ghostnetfishing.service;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.*;
import dev.floelly.ghostnetfishing.repository.NetRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.getRandomUserName;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetServiceTest {
    @Mock
    private NetRepository netRepository;

    @Mock
    private IUserService userService;

    @InjectMocks
    private NetService netService;

    @Nested
    class SaveNewNetTests {

        @Test
        void shouldSaveNewNetUsingRepository() {
            // given
            NewNetRequest request = new NewNetRequest(12.34, 56.78, NetSize.L);

            // when
            netService.addNewNet(request);
            Net savedNet = captureSavedNet();

            // then
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
    }

    @Nested
    class GetAllTests {

        @Test
        void shouldReturnAllSavedNetDTOs_onGetAll() {
            //given
            List<Net> sortedNets = Stream.of(
                    createDefaultNet(NetState.REPORTED),
                    createDefaultNet(NetState.RECOVERY_PENDING)
            ).sorted(Comparator.comparing(Net::getNetId)).toList();
            when(netRepository.findAll()).thenReturn(sortedNets);

            //when
            List<NetDTO> sortedNetDTOs = netService.getAll().stream()
                    .sorted(Comparator.comparing(NetDTO::getId))
                    .toList();

            //then
            assertThat(sortedNetDTOs.size()).isEqualTo(sortedNets.size());
            for(int i = 0; i < sortedNets.size(); i++){
                assertNetDTOEquals(sortedNetDTOs.get(i), sortedNets.get(i));
            }
        }
    }

    @Nested
    class RequestRecoveryTests {

        @Test
        void shouldUpdateRepository_whenNetStateIsReportedAndWithoutUser_onRequestRecovery() {
            // given
            User user = createDefaultUser();
            Net databaseNet = createDefaultNet(NetState.REPORTED);
            mockNetFound(databaseNet);
            mockUserFound(user);

            // when
            netService.requestRecovery(databaseNet.getNetId(), user.getUsername());
            Net updatedNet = captureSavedNet();

            // then
            assertNetEquals(updatedNet, databaseNet);
            assertThat(updatedNet.getState())
                    .as("should update net state.")
                    .isEqualTo(NetState.RECOVERY_PENDING);
            assertThat(updatedNet.getUser().getId())
                    .as(String.format("should be owned by user with id %s. Given user: %s.", user.getId(), updatedNet.getUser().getId()))
                    .isEqualTo(user.getId());
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService).findByUsername(eq(user.getUsername()));
        }

        @Test
        void shouldThrowIllegalNetStateException_whenUserGivenWithoutRecoveryPendingState_OnRequestRecovery() {
            // given
            Net databaseNet = createDefaultNet(NetState.REPORTED);
            databaseNet.setUser(createDefaultUser());
            mockNetFound(databaseNet);

            // then
            assertThrows(IllegalNetStateException.class, () -> netService.requestRecovery(databaseNet.getNetId(), getRandomUserName()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }

        @Test
        void shouldThrowAccessDeniedException_whenNoPhoneNumberOnUser_OnRequestRecovery() {
            // given
            User user = createDefaultUser();
            user.setPhone(null);
            Net databaseNet = createDefaultNet(NetState.REPORTED);
            mockNetFound(databaseNet);
            mockUserFound(user);

            // then
            assertThrows(AccessDeniedException.class, () -> netService.requestRecovery(databaseNet.getNetId(), user.getUsername()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService).findByUsername(eq(user.getUsername()));
            verify(netRepository, never()).save(any());
        }

        @Test
        void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnRequestRecovery() {
            // given
            mockNoNetFound();
            Long netId = getRandomNetId();

            // then
            assertThrows(NetNotFoundException.class, () -> netService.requestRecovery(netId, getRandomUserName()));
            verify(netRepository).findByNetId(eq(netId));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED", "RECOVERY_PENDING"})
        void shouldThrowIllegalNetStateChange_whenInvalidState_onRequestRecovery(NetState state) {
            // given
            Net databaseNet = createDefaultNet(state);
            mockNetFound(databaseNet);

            // then
            assertThrows(IllegalNetStateChangeException.class, () -> netService.requestRecovery(databaseNet.getNetId(), getRandomUserName()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }
    }

    @Nested
    class MarkRecoveredTests {

        @ParameterizedTest
        @EnumSource(value = NetState.class, names = {"RECOVERY_PENDING", "REPORTED"})
        void shouldUpdateRepository_whenNetStateIsValid_onMarkRecovered(NetState state) {
            // given
            Net databaseNet = createDefaultNet(state);
            User user = state.equals(NetState.RECOVERY_PENDING) ? databaseNet.getUser() : createDefaultUser();
            mockNetFound(databaseNet);
            mockUserFound(user);

            // when
            netService.markRecovered(databaseNet.getNetId(), user.getUsername());
            Net updatedNet = captureSavedNet();

            // then
            assertNetEquals(updatedNet, databaseNet);
            assertThat(updatedNet.getState())
                    .as("should update net state.")
                    .isEqualTo(NetState.RECOVERED);
            assertThat(updatedNet.getUser())
                    .as(String.format("should not be owned by any one. Given user: %s.", updatedNet.getUser()))
                    .isNull();
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService).findByUsername(eq(user.getUsername()));
        }

        @Test
        void shouldThrowIllegalNetStateException_whenRecoveryPendingWithoutUser_OnMarkRecovered() {
            // given
            Net databaseNet = createDefaultNet(NetState.RECOVERY_PENDING);
            databaseNet.setUser(null);
            mockNetFound(databaseNet);

            // then
            assertThrows(IllegalNetStateException.class, () -> netService.markRecovered(databaseNet.getNetId(), getRandomUserName()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }

        @Test
        void shouldThrowAccessDeniedException_whenNotOwningNet_OnMarkRecovered() {
            // given
            User user = createDefaultUser();
            Net databaseNet = createDefaultNet(NetState.RECOVERY_PENDING);
            mockNetFound(databaseNet);
            mockUserFound(user);

            // then
            assertThrows(AccessDeniedException.class, () -> netService.markRecovered(databaseNet.getNetId(), user.getUsername()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService).findByUsername(eq(user.getUsername()));
            verify(netRepository, never()).save(any());
        }

        @Test
        void shouldThrowAccessDeniedException_whenNoPhoneNumberOnUser_OnMarkRecovered() {
            // given
            Net databaseNet = createDefaultNet(NetState.RECOVERY_PENDING);
            User user = databaseNet.getUser();
            user.setPhone(null);
            mockNetFound(databaseNet);
            mockUserFound(user);

            // then
            assertThrows(AccessDeniedException.class, () -> netService.markRecovered(databaseNet.getNetId(), user.getUsername()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService).findByUsername(eq(user.getUsername()));
            verify(netRepository, never()).save(any());
        }

        @Test
        void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnMarkRecovered() {
            // given
            Long netId = getRandomNetId();
            mockNoNetFound();

            // then
            assertThrows(NetNotFoundException.class, () -> netService.markRecovered(netId, null));
            verify(netRepository).findByNetId(eq(netId));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED"})
        void shouldThrowIllegalNetStateChange_whenInvalidState_onMarkRecovered(NetState state) {
            // given
            Net databaseNet = createDefaultNet(state);
            mockNetFound(databaseNet);

            // then
            assertThrows(IllegalNetStateChangeException.class, () -> netService.markRecovered(databaseNet.getNetId(), null));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(userService, never()).findByUsername(any());
            verify(netRepository, never()).save(any());
        }
    }

    @Nested
    class MarkLostTests {

        @ParameterizedTest
        @EnumSource(value = NetState.class, names = {"RECOVERY_PENDING", "REPORTED"})
        void shouldUpdateRepository_whenNetStateIsValid_onMarkLost(NetState state) {
            // given
            Net databaseNet = createDefaultNet(state);
            Long netId = databaseNet.getNetId();
            mockNetFound(databaseNet);

            // when
            netService.markLost(netId);
            Net updatedNet = captureSavedNet();

            // then
            assertNetEquals(updatedNet, databaseNet);
            assertThat(updatedNet.getState())
                    .as("should update net state.")
                    .isEqualTo(NetState.LOST);
            assertThat(updatedNet.getUser())
                    .as("should remove attached user.")
                    .isNull();
            verify(netRepository).findByNetId(eq(netId));
        }

        @Test
        void shouldThrowNetNotFoundException_whenNoNetWithNetId_OnMarkLost() {
            // given
            Long netId = getRandomNetId();
            mockNoNetFound();

            // then
            assertThrows(NetNotFoundException.class, () -> netService.markLost(netId));
            verify(netRepository).findByNetId(eq(netId));
            verify(netRepository, never()).save(any());
        }

        @ParameterizedTest
        @EnumSource(value = NetState.class, names = {"LOST", "RECOVERED"})
        void shouldThrowIllegalNetStateChange_whenInvalidState_onMarkLost(NetState state) {
            // given
            Net databaseNet = createDefaultNet(state);
            mockNetFound(databaseNet);

            // then
            assertThrows(IllegalNetStateChangeException.class, () -> netService.markLost(databaseNet.getNetId()));
            verify(netRepository).findByNetId(eq(databaseNet.getNetId()));
            verify(netRepository, never()).save(any());
        }
    }

    private static void assertNetEquals(Net updatedNet, Net databaseNet) {
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
    }

    private void assertNetDTOEquals(NetDTO dto, Net net) {
        assertThat(dto.getId()).isEqualTo(net.getNetId());
        assertThat(dto.getLocationLat()).isEqualTo(net.getLocationLat());
        assertThat(dto.getLocationLong()).isEqualTo(net.getLocationLong());
        assertThat(dto.getSize()).isEqualTo(net.getSize());
        assertThat(dto.getState()).isEqualTo(net.getState());
        assertThat(dto.getOwner()).isEqualTo(net.getUser() != null ? net.getUser().getUsername() : null);
    }

    private Net captureSavedNet() {
        ArgumentCaptor<Net> netCaptor = ArgumentCaptor.forClass(Net.class);
        verify(netRepository).save(netCaptor.capture());
        return netCaptor.getValue();
    }

    private void mockNetFound(Net databaseNet) {
        when(netRepository.findByNetId(eq(databaseNet.getNetId()))).thenReturn(Optional.of(databaseNet));
    }

    private void mockUserFound(User user) {
        when(userService.findByUsername(eq(user.getUsername()))).thenReturn(user);
    }

    private void mockNoNetFound() {
        when(netRepository.findByNetId(any())).thenReturn(Optional.empty());
    }
}