package com.smartnotes.app.backend.aspect;
import com.smartnotes.app.backend.entity.User;
import com.smartnotes.app.backend.exception.UnauthorizedNoteAccessException;
import com.smartnotes.app.backend.repository.Owned;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.smartnotes.app.backend.annotation.RequireOwnership;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class OwnershipAspect {

    private final EntityManager entityManager;

    @Around(
            value = "@within(requireOwnership) && execution(public * *(java.util.UUID,..))",
            argNames = "pjp,requireOwnership"
    )
    public Object check(ProceedingJoinPoint pjp, RequireOwnership requireOwnership) throws Throwable {

        // Arrange
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] names = signature.getParameterNames();
        Object[] args = pjp.getArgs();

        // Look for the parameter and assign its value to the id variable
        UUID id = null;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(requireOwnership.idParam())) {
                id = (UUID) args[i];
            }
        }

        // Check if id param exists or not
        if (id == null) {
            return pjp.proceed();
        }

        // Get the current user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Get the entity, e.g. the note
        Owned entity = entityManager.find(requireOwnership.entity(), id);

        // Check if its owner is the current user or not
        // throw error if not
        if (entity != null && !entity.getOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedNoteAccessException(id, currentUser.getId());
        }

        // proceed if it is
        return pjp.proceed();
    }

}
