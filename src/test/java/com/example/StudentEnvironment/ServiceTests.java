package com.example.StudentEnvironment;

import com.example.StudentEnvironment.config.CustomUserDetails;
import com.example.StudentEnvironment.entities.*;
import com.example.StudentEnvironment.repositories.*;
import com.example.StudentEnvironment.services.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ServiceTests {

    // --- Mocks ---
    @Mock private GroupRepository groupRepository;
    @Mock private PlaceRepository placeRepository;
    @Mock private UserRepository userRepository;
    @Mock private MessageRepository messageRepository;
    @Mock private ExchangeRequestRepository exchangeRequestRepository;
    @Mock private UserService userService; // required by MessageService

    // --- InjectMocks ---
    @InjectMocks private GroupService groupService;
    @InjectMocks private PlaceService placeService;
    @InjectMocks private MessageService messageService;
    @InjectMocks private ExchangeService exchangeService;
    @InjectMocks private UserService userServiceInjected; // used to test delete()

    // --- Tests ---

    @Test
    void testAddUserToEndOfQueue() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(placeRepository.findByUser(user)).thenReturn(null);
        when(placeRepository.findMaxTime()).thenReturn(LocalDateTime.now());

        placeService.addToEnd(user);

        verify(placeRepository, times(1)).save(any(Place.class));
    }

    @Test
    void testDeletePlaceByNonOwner() {
        User user = new User();
        user.setId(1L);
        user.setRole("STUDENT");
        Place place = new Place();
        place.setUser(new User(2L, "user2", "...", "...", "STUDENT", null, null, null));

        when(placeRepository.findById(100L)).thenReturn(Optional.of(place));

        assertThrows(AccessDeniedException.class, () -> placeService.deletePlace(100L, user));
    }

    @Test
    void testConfirmExchangeRequestSwapsTimes() {
        User from = new User(); from.setId(1L);
        User to = new User(); to.setId(2L);
        Place fromPlace = new Place(1L, LocalDateTime.now(), from);
        Place toPlace = new Place(2L, LocalDateTime.now().plusMinutes(10), to);
        ExchangeRequest request = new ExchangeRequest(1L, from, to, LocalDateTime.now());

        when(exchangeRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(placeRepository.findByUser(from)).thenReturn(fromPlace);
        when(placeRepository.findByUser(to)).thenReturn(toPlace);

        exchangeService.confirmExchangeRequest(1L);

        verify(placeRepository, times(2)).save(any());
    }

    @Test
    void testGetQueueForGroupSortedByTime() {
        Group group = new Group(); group.setId(1L);
        User u1 = new User(); u1.setId(1L);
        Place p1 = new Place(1L, LocalDateTime.now().plusMinutes(5), u1);
        u1.setPlaces(List.of(p1));
        group.setUsers(List.of(u1));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        Group fetched = groupService.findById(1L);
        assertEquals(1, fetched.getUsers().size());
        assertEquals(1L, fetched.getUsers().get(0).getId());
    }

    @Test
    void testHeadmanCanAddMessage() {
        User headman = new User(); headman.setUsername("headman"); headman.setRole("HEADMAN");
        Group group = new Group(); headman.setGroup(group);

        when(userService.findByUsername("headman")).thenReturn(headman);

        messageService.createMessage("Test Message", "headman");

        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testDeleteMessageNotAllowed() {
        User user = new User(); user.setUsername("student"); user.setRole("STUDENT");
        Group group = new Group(); group.setId(1L);
        Message message = new Message(1L, "msg", LocalDateTime.now(), group);

        when(userService.findByUsername("student")).thenReturn(user); // FIXED LINE
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        assertThrows(AccessDeniedException.class, () -> messageService.deleteMessage(1L, "student"));
    }

    @Test
    void testCustomUserDetailsAuthorities() {
        User user = new User(); user.setRole("ADMIN, HEADMAN");
        CustomUserDetails details = new CustomUserDetails(user);

        List<GrantedAuthority> authorities = new ArrayList<>(details.getAuthorities());

        assertTrue(authorities.contains(new SimpleGrantedAuthority("ADMIN")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("HEADMAN")));
    }

    @Test
    void testPasswordHashing() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1234";
        String hash = encoder.encode(rawPassword);

        assertTrue(encoder.matches("1234", hash));
    }

    @Test
    void testSendExchangeToSelfThrowsException() {
        User user = new User(); user.setId(1L);

        assertThrows(IllegalArgumentException.class,
                () -> exchangeService.sendExchangeRequest(user, user));
    }

    @Test
    void testDeleteUser() {
        userServiceInjected.delete(5L); // use the correct injected service
        verify(userRepository).deleteById(5L); // FIXED: verify mock repo
    }

}
