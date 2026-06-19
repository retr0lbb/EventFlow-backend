package spring.infra.api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring.infra.api.dtos.auth.SignupRequest;
import spring.infra.api.dtos.auth.SignupResponse;
import spring.infra.api.exceptions.EmailAlreadyExistsException;
import spring.infra.api.exceptions.InvalidRoleCombinationException;
import spring.infra.api.models.Role;
import spring.infra.api.models.User;
import spring.infra.api.repository.RoleRepository;
import spring.infra.api.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Role visitantRole;
    private Role standCreatorRole;
    private Role eventCreatorRole;

    @BeforeEach
    void setUp() {
        visitantRole = new Role();
        visitantRole.setRoleId(1L);
        visitantRole.setName("VISITANT");

        standCreatorRole = new Role();
        standCreatorRole.setRoleId(2L);
        standCreatorRole.setName("STAND_CREATOR");

        eventCreatorRole = new Role();
        eventCreatorRole.setRoleId(3L);
        eventCreatorRole.setName("EVENT_CREATOR");
    }

    @Test
    void signup_WithValidRequest_ShouldCreateUser() {

        SignupRequest request = new SignupRequest("test@test.com", "senha123", "test", "12345");
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPhone(request.phone());

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(visitantRole));
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        SignupResponse response = userService.signup(request);

        // Assert
        assertNotNull(response);
        assertEquals(user.getId(), response.id());
        assertEquals(request.email(), response.email());
        assertEquals(request.name(), response.name());
        
        verify(userRepository, times(1)).findByEmail(request.email());
        verify(roleRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode(request.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void signup_WithDuplicateEmail_ShouldThrowEmailAlreadyExistsException() {
        // Arrange
        SignupRequest request = new SignupRequest("adrian@test.com", "senha123", "Adrian", "12345");
        User existingUser = new User();

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.signup(request));
        
        verify(userRepository, times(1)).findByEmail(request.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signup_WithMissingDefaultRole_ShouldThrowIllegalStateException() {
        // Arrange
        SignupRequest request = new SignupRequest("adrian@test.com", "senha123", "Adrian", "12345");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userService.signup(request));
        
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateRolesCombination_WithVisitantAndStandCreator_ShouldThrowInvalidRoleCombinationException() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(visitantRole);
        roles.add(standCreatorRole);

        // Act & Assert
        assertThrows(InvalidRoleCombinationException.class, () -> userService.validateRolesCombination(roles));
    }

    @Test
    void validateRolesCombination_WithVisitantAndEventCreator_ShouldThrowInvalidRoleCombinationException() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(visitantRole);
        roles.add(eventCreatorRole);

        // Act & Assert
        assertThrows(InvalidRoleCombinationException.class, () -> userService.validateRolesCombination(roles));
    }

    @Test
    void validateRolesCombination_WithStandCreatorAndEventCreator_ShouldSucceed() {
        // Arrange
        Set<Role> roles = new HashSet<>();
        roles.add(standCreatorRole);
        roles.add(eventCreatorRole);

        // Act & Assert
        assertDoesNotThrow(() -> userService.validateRolesCombination(roles));
    }
}
