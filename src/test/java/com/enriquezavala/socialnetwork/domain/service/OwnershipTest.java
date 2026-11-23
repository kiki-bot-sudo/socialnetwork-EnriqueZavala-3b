package com.enriquezavala.socialnetwork.domain.service;

import com.enriquezavala.socialnetwork.domain.exception.ForbiddenOperationException;
import com.enriquezavala.socialnetwork.domain.model.Photograph;
import com.enriquezavala.socialnetwork.domain.model.User;
import com.enriquezavala.socialnetwork.domain.repository.ImageStorage;
import com.enriquezavala.socialnetwork.domain.repository.PhotographRepository;
import com.enriquezavala.socialnetwork.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OwnershipTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PhotographRepository photographRepository = mock(PhotographRepository.class);
    private final PhotographService photographService =
            new PhotographService(photographRepository, userRepository, mock(ImageStorage.class));

    private static User user(long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }

    @Test
    void photographOwnerComesFromTokenNotFromBody() {
        when(userRepository.findByEmail("me@example.com"))
                .thenReturn(Optional.of(user(1L, "me@example.com")));
        when(photographRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Photograph photo = new Photograph();
        photo.setId(99L);
        photo.setTitle("Atardecer");
        photo.setImageUrl("https://example.com/a.jpg");
        photo.setCategory("paisaje"); // en minúsculas: el servicio la normaliza
        photo.setUser(user(2L, "other@example.com"));

        photographService.save(photo, "me@example.com");

        ArgumentCaptor<Photograph> saved = ArgumentCaptor.forClass(Photograph.class);
        verify(photographRepository).save(saved.capture());
        assertEquals(1L, saved.getValue().getUser().getId());
        assertNull(saved.getValue().getId());
        assertEquals("Paisaje", saved.getValue().getCategory());
    }

    @Test
    void rejectsUnknownCategory() {
        Photograph photo = new Photograph();
        photo.setTitle("Foto");
        photo.setImageUrl("https://example.com/b.jpg");
        photo.setCategory("Inventada");

        assertThrows(IllegalArgumentException.class,
                () -> photographService.save(photo, "me@example.com"));
        verify(photographRepository, never()).save(any());
    }

    @Test
    void cannotDeleteSomeoneElsesPhotograph() {
        Photograph photo = new Photograph();
        photo.setId(5L);
        photo.setUser(user(2L, "other@example.com"));
        when(photographRepository.findById(5L)).thenReturn(Optional.of(photo));
        when(userRepository.findByEmail("me@example.com"))
                .thenReturn(Optional.of(user(1L, "me@example.com")));

        assertThrows(ForbiddenOperationException.class,
                () -> photographService.deleteById(5L, "me@example.com"));
        verify(photographRepository, never()).deleteById(any());
    }

    @Test
    void cannotDeleteAnotherUserAccount() {
        UserService userService = new UserService(userRepository, new BCryptPasswordEncoder());
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L, "other@example.com")));
        when(userRepository.findByEmail("me@example.com"))
                .thenReturn(Optional.of(user(1L, "me@example.com")));

        assertThrows(ForbiddenOperationException.class,
                () -> userService.deleteById(2L, "me@example.com"));
        verify(userRepository, never()).deleteById(any());
    }
}
