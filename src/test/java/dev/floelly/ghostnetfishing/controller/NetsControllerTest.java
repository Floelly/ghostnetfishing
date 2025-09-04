package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.service.INetService;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;

import static dev.floelly.ghostnetfishing.testutil.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetsControllerTest {
    public static final NewNetRequest VALID_NEW_NET_REQUEST = new NewNetRequest(20.0, 20.0, NetSize.L);
    public static final NetDTO VALID_NET_DTO = new NetDTO(5L, 20.0, 20.0, NetSize.L, NetState.RECOVERY_PENDING, "Gustav");

    @Mock
    INetService netService;

    @Mock
    UserDetails userDetails;

    @Mock
    RedirectAttributes redirectAttributes;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @InjectMocks
    private NetsController netController;

    @Test
    void shouldReturnNewNetsContent_onGetNewNetForm() {
        String controllerResponse = netController.getNewNetFormPage(model);
        assertEquals(NEW_NET_CONTENT_TEMPLATE, controllerResponse, String.format("The response of the controller should be '%s'", NEW_NET_CONTENT_TEMPLATE));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onPostNewNet() {
        doNothing().when(netService).addNewNet(eq(VALID_NEW_NET_REQUEST));

        String viewName = netController.postNewNet(VALID_NEW_NET_REQUEST, bindingResult, model, redirectAttributes);

        verify(netService).addNewNet(eq(VALID_NEW_NET_REQUEST));
        assertEquals("redirect:" + POST_NEW_NET_REDIRECT_TEMPLATE, viewName, String.format("The response of the controller should be a 'redirect:%s'", POST_NEW_NET_REDIRECT_TEMPLATE));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onPostNewNet(){
        doThrow(new IllegalArgumentException("invalid net"))
                .when(netService).addNewNet(eq(VALID_NEW_NET_REQUEST));

        assertThrows(IllegalArgumentException.class, () ->
                netController.postNewNet(VALID_NEW_NET_REQUEST, bindingResult, model, redirectAttributes));
    }

    @Test
    void shouldCallServiceAndReturnNetsContent_onGetNetsPage() {
        Model model = new ExtendedModelMap();
        when(netService.getAll())
                .thenReturn(List.of(VALID_NET_DTO));

        String viewName = netController.getNetsPage(model, null);

        assertThat(viewName).isEqualTo(NETS_CONTENT_TEMPLATE);
        assertThat(model.getAttribute(NETS_THYMELEAF_ATTRIBUTE))
                .asInstanceOf(InstanceOfAssertFactories.list(NetDTO.class))
                .hasSize(1)
                .allMatch(Objects::nonNull)
                .extracting(NetDTO::getId)
                .contains(VALID_NET_DTO.getId());
        verify(netService).getAll();
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onGetNetsPageWithState(){
        doThrow(new RuntimeException()).when(netService).getAllByState(eq(NetState.RECOVERY_PENDING));

        assertThrows(RuntimeException.class, () ->
                netController.getNetsPage(model, NetState.RECOVERY_PENDING));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onGetNetsPage(){
        doThrow(new RuntimeException()).when(netService).getAll();

        assertThrows(RuntimeException.class, () ->
                netController.getNetsPage(model, null));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onRequestNetRecovery() {
        when(userDetails.getUsername()).thenReturn(USERNAME);

        doThrow(new RuntimeException()).when(netService).requestRecovery(any(), any());

        assertThrows(RuntimeException.class, () ->
                netController.requestNetRecovery(5L, redirectAttributes, userDetails));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onRequestNetRecovery() {
        Long netId = getRandomNetId();
        when(userDetails.getUsername()).thenReturn(USERNAME);
        doNothing().when(netService).requestRecovery(eq(netId), eq(USERNAME));

        String controllerResponse = netController.requestNetRecovery(netId, redirectAttributes, userDetails);

        verify(netService).requestRecovery(eq(netId), eq(USERNAME));
        verify(userDetails).getUsername();
        assertEquals("redirect:" + REQUEST_RECOVERY_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", REQUEST_RECOVERY_REDIRECT_TEMPLATE));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onMarkNetRecovered() {
        when(userDetails.getUsername()).thenReturn(USERNAME);
        doThrow(new RuntimeException()).when(netService).markRecovered(any(), any());

        assertThrows(RuntimeException.class, () ->
                netController.markRecovered(5L, redirectAttributes, userDetails));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onMarkNetRecovered () {
        Long netId = getRandomNetId();
        when(userDetails.getUsername()).thenReturn(USERNAME);
        doNothing().when(netService).markRecovered(eq(netId), eq(USERNAME));

        String controllerResponse = netController.markRecovered(netId, redirectAttributes, userDetails);

        verify(netService).markRecovered(eq(netId), eq(USERNAME));
        verify(userDetails).getUsername();
        assertEquals("redirect:" + MARK_RECOVERED_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", MARK_RECOVERED_REDIRECT_TEMPLATE));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onMarkNetLost() {
        doThrow(new RuntimeException()).when(netService).markLost(any());

        assertThrows(RuntimeException.class, () ->
                netController.markLost(5L, redirectAttributes));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onMarkNetLost () {
        Long netId = getRandomNetId();
        doNothing().when(netService).markLost(eq(netId));

        String controllerResponse = netController.markLost(netId, redirectAttributes);

        verify(netService).markLost(eq(netId));
        assertEquals("redirect:" + MARK_LOST_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", MARK_LOST_REDIRECT_TEMPLATE));
    }
}