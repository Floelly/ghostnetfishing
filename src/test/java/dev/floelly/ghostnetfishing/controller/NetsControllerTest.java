package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetSize;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.service.INewNetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetsControllerTest {
    private final static String NEW_NET_CONTENT_TEMPLATE = "/nets/new";
    private static final String NETS_CONTENT_TEMPLATE = "/nets";
    private static final String POST_NEW_NET_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;

    private static final String NETS_THYMELEAF_ATTRIBUTE = "nets";
    public static final NewNetRequest VALID_NEW_NET_REQUEST = new NewNetRequest(20.0, 20.0, NetSize.L);
    public static final NetDTO VALID_NET_DTO = new NetDTO(5L, 20.0, 20.0, NetSize.L, NetState.RECOVERY_PENDING);

    @Mock
    INewNetService newNetService;

    @InjectMocks
    private NetsController netController;

    @Test
    void shouldReturnNewNetsContent_onGetNewNetForm() {
        String controllerResponse = netController.getNewNetFormPage();
        assertEquals(NEW_NET_CONTENT_TEMPLATE, controllerResponse, String.format("The response of the controller should be '%s'", NEW_NET_CONTENT_TEMPLATE));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onPostNewNet() {
        BindingResult bindingResult = new BeanPropertyBindingResult(VALID_NEW_NET_REQUEST, "newNet");
        Model model = new ExtendedModelMap();
        doNothing().when(newNetService).addNewNet(eq(VALID_NEW_NET_REQUEST));

        String controllerResponse = netController.postNewNet(VALID_NEW_NET_REQUEST, bindingResult, model);

        verify(newNetService).addNewNet(eq(VALID_NEW_NET_REQUEST));
        assertEquals("redirect:" + POST_NEW_NET_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", POST_NEW_NET_REDIRECT_TEMPLATE));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onPostNewNet(){
        BindingResult bindingResult = new BeanPropertyBindingResult(VALID_NEW_NET_REQUEST, "newNet");
        Model model = new ExtendedModelMap();

        doThrow(new IllegalArgumentException("invalid net"))
                .when(newNetService).addNewNet(eq(VALID_NEW_NET_REQUEST));

        assertThrows(IllegalArgumentException.class, () ->
                netController.postNewNet(VALID_NEW_NET_REQUEST, bindingResult, model));
    }

    @Test
    void shouldCallServiceAndReturnNetsContent_onGetNetsPage() {
        Model model = new ExtendedModelMap();
        when(newNetService.getAll())
                .thenReturn(List.of(VALID_NET_DTO));

        String controllerResponse = netController.getNetsPage(model);

        verify(newNetService).getAll();
        assertEquals(NETS_CONTENT_TEMPLATE, controllerResponse);
        Object netsAttribute = model.getAttribute(NETS_THYMELEAF_ATTRIBUTE);
        assertNotNull(netsAttribute);
        assertInstanceOf(List.class, netsAttribute);
        @SuppressWarnings("unchecked")
        List<NetDTO> nets = (List<NetDTO>) netsAttribute;
        assertTrue(nets.stream().allMatch(Objects::nonNull));
        assertEquals(1, nets.size());
        assertThat(nets)
                .extracting(NetDTO::getId)
                .contains(VALID_NET_DTO.getId());
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onGetNetsPage(){
        Model model = new ExtendedModelMap();

        doThrow(new RuntimeException()).when(newNetService).getAll();

        assertThrows(RuntimeException.class, () ->
                netController.getNetsPage(model));
    }
}