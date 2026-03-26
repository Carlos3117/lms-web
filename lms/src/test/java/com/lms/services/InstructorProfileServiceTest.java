package com.lms.services;

import com.lms.entities.Instructor;
import com.lms.entities.InstructorProfile;
import com.lms.repositories.InstructorProfileRepository;
import com.lms.repositories.InstructorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorProfileServiceTest {

    @Mock
    private InstructorProfileRepository profileRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @InjectMocks
    private InstructorProfilleServiceImpl profileService;


    @Test
    void shouldCreateProfile() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);
        profile.setPhone("123");
        profile.setBio("Bio");

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(profileRepository.findAll()).thenReturn(List.of());
        when(profileRepository.save(any())).thenReturn(profile);

        InstructorProfile result = profileService.create(profile);

        assertNotNull(result);
        assertEquals("123", result.getPhone());
    }

    @Test
    void shouldThrowIfProfileIsNull() {

        assertThrows(InstructorProfileService.ProfileInvalidException.class, () -> {
            profileService.create(null);
        });
    }

    @Test
    void shouldThrowIfInstructorNotExists() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);
        profile.setPhone("123");
        profile.setBio("Bio");

        when(instructorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InstructorProfileService.ProfileInvalidException.class, () -> {
            profileService.create(profile);
        });
    }

    @Test
    void shouldThrowIfProfileAlreadyExists() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile existing = new InstructorProfile();
        existing.setInstructor(instructor);

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);
        profile.setPhone("123");
        profile.setBio("Bio");

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(profileRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(InstructorProfileService.ProfileAlreadyExistsException.class, () -> {
            profileService.create(profile);
        });
    }

    @Test
    void shouldThrowIfPhoneOrBioInvalid() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(profileRepository.findAll()).thenReturn(List.of());

        assertThrows(InstructorProfileService.ProfileInvalidException.class, () -> {
            profileService.create(profile);
        });
    }


    @Test
    void shouldFindById() {

        InstructorProfile profile = new InstructorProfile();
        profile.setId(1L);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        InstructorProfile result = profileService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfNotFound() {

        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InstructorProfileService.ProfileNotFoundException.class, () -> {
            profileService.findById(1L);
        });
    }



    @Test
    void shouldUpdateProfile() {

        InstructorProfile existing = new InstructorProfile();
        existing.setId(1L);
        existing.setPhone("old");
        existing.setBio("old");

        InstructorProfile updated = new InstructorProfile();
        updated.setPhone("new");
        updated.setBio("new");

        when(profileRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(profileRepository.save(any())).thenReturn(existing);

        InstructorProfile result = profileService.update(1L, updated);

        assertEquals("new", result.getPhone());
        assertEquals("new", result.getBio());
    }



    @Test
    void shouldDeleteProfile() {

        InstructorProfile profile = new InstructorProfile();
        profile.setId(1L);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.delete(1L);

        verify(profileRepository).delete(profile);
    }


    @Test
    void shouldFindByInstructor() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);

        when(profileRepository.findAll()).thenReturn(List.of(profile));

        InstructorProfile result = profileService.findByInstructor(1L);

        assertNotNull(result);
    }

    @Test
    void shouldThrowIfProfileByInstructorNotFound() {

        when(profileRepository.findAll()).thenReturn(List.of());

        assertThrows(InstructorProfileService.ProfileNotFoundException.class, () -> {
            profileService.findByInstructor(1L);
        });
    }



    @Test
    void shouldCheckExistsByInstructor() {

        Instructor instructor = Instructor.builder().id(1L).build();

        InstructorProfile profile = new InstructorProfile();
        profile.setInstructor(instructor);

        when(profileRepository.findAll()).thenReturn(List.of(profile));

        assertTrue(profileService.existsByInstructor(1L));
    }
}