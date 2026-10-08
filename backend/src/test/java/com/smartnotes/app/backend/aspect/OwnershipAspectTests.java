package com.smartnotes.app.backend.aspect;

import com.smartnotes.app.backend.annotation.RequireOwnership;
import com.smartnotes.app.backend.entity.Note;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.UnauthorizedNoteAccessException;
import com.smartnotes.app.backend.repository.Owned;
import jakarta.persistence.EntityManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnershipAspectTests {

    @Mock
    private EntityManager entityManager;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private RequireOwnership requireOwnership;

    @InjectMocks
    private OwnershipAspect ownershipAspect;

    private User currentUser;
    private UUID currentUserId;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        currentUser = new User();
        currentUser.setId(currentUserId);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockAuthenticatedUser(User user) {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void OwnershipAspect_OwnerMatches_ProceedsSuccessfully() throws Throwable {
        // Arrange
        UUID targetId = UUID.randomUUID();
        Note note = new Note();
        note.setId(targetId);
        note.setOwner(currentUser);

        mockAuthenticatedUser(currentUser);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{targetId});
        when(requireOwnership.idParam()).thenReturn("id");
        doReturn(Note.class).when(requireOwnership).entity();
        when(entityManager.find(Note.class, targetId)).thenReturn(note);
        when(joinPoint.proceed()).thenReturn("success");

        // Act
        Object result = ownershipAspect.check(joinPoint, requireOwnership);

        // Assert
        Assertions.assertThat(result).isEqualTo("success");
        verify(joinPoint).proceed();
    }

    @Test
    void OwnershipAspect_OwnerDoesNotMatch_ThrowsUnauthorizedNoteAccessException() throws Throwable {
        // Arrange
        UUID targetId = UUID.randomUUID();
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Note note = new Note();
        note.setId(targetId);
        note.setOwner(otherUser);

        mockAuthenticatedUser(currentUser);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{targetId});
        when(requireOwnership.idParam()).thenReturn("id");
        doReturn(Note.class).when(requireOwnership).entity();
        when(entityManager.find(Note.class, targetId)).thenReturn(note);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> ownershipAspect.check(joinPoint, requireOwnership))
                .isInstanceOf(UnauthorizedNoteAccessException.class);

        verify(joinPoint, never()).proceed();
    }

    @Test
    void OwnershipAspect_EntityNull_ProceedsSuccessfully() throws Throwable {
        // Arrange
        UUID targetId = UUID.randomUUID();

        mockAuthenticatedUser(currentUser);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"id"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{targetId});
        when(requireOwnership.idParam()).thenReturn("id");
        doReturn(Note.class).when(requireOwnership).entity();
        when(entityManager.find(Note.class, targetId)).thenReturn(null);
        when(joinPoint.proceed()).thenReturn("proceeded-null-entity");

        // Act
        Object result = ownershipAspect.check(joinPoint, requireOwnership);

        // Assert
        Assertions.assertThat(result).isEqualTo("proceeded-null-entity");
        verify(joinPoint).proceed();
    }

    @Test
    void OwnershipAspect_NoMatchingIdParam_ProceedsSuccessfully() throws Throwable {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"otherParam"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"someValue"});
        when(requireOwnership.idParam()).thenReturn("id");
        when(joinPoint.proceed()).thenReturn("proceeded-no-id");

        // Act
        Object result = ownershipAspect.check(joinPoint, requireOwnership);

        // Assert
        Assertions.assertThat(result).isEqualTo("proceeded-no-id");
        verify(joinPoint).proceed();
    }
}
