package com.lms.services;

import com.lms.entities.InstructorProfile;
import com.lms.repositories.InstructorProfileRepository;
import com.lms.repositories.InstructorRepository;
import com.lms.services.InstructorProfileService;

import java.util.List;

public class InstructorProfilleServiceImpl implements InstructorProfileService {

    private final InstructorProfileRepository profileRepository;
    private final InstructorRepository instructorRepository;

    public InstructorProfilleServiceImpl(InstructorProfileRepository profileRepository,
                                        InstructorRepository instructorRepository) {
        this.profileRepository = profileRepository;
        this.instructorRepository = instructorRepository;
    }

    @Override
    public InstructorProfile create(InstructorProfile profile) {

        if (profile == null)
            throw new ProfileInvalidException("Profile null");

        if (profile.getInstructor() == null || profile.getInstructor().getId() == null)
            throw new ProfileInvalidException("Instructor requerido");

        Long instructorId = profile.getInstructor().getId();

        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ProfileInvalidException("Instructor no existe"));

        if (existsByInstructor(instructorId))
            throw new ProfileAlreadyExistsException("Ya existe perfil");

        if (profile.getPhone() == null || profile.getPhone().isBlank())
            throw new ProfileInvalidException("Teléfono requerido");

        if (profile.getBio() == null || profile.getBio().isBlank())
            throw new ProfileInvalidException("Bio requerida");

        return profileRepository.save(profile);
    }

    @Override
    public InstructorProfile update(Long id, InstructorProfile profile) {

        InstructorProfile existing = findById(id);

        if (profile.getPhone() != null)
            existing.setPhone(profile.getPhone());

        if (profile.getBio() != null)
            existing.setBio(profile.getBio());

        return profileRepository.save(existing);
    }

    @Override
    public InstructorProfile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException("No encontrado"));
    }

    @Override
    public List<InstructorProfile> findAll() {
        return profileRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        profileRepository.delete(findById(id));
    }

    @Override
    public InstructorProfile findByInstructor(Long instructorId) {

        return profileRepository.findAll().stream()
                .filter(p -> p.getInstructor().getId().equals(instructorId))
                .findFirst()
                .orElseThrow(() -> new ProfileNotFoundException("No encontrado"));
    }

    @Override
    public boolean existsByInstructor(Long instructorId) {
        return profileRepository.findAll().stream()
                .anyMatch(p -> p.getInstructor().getId().equals(instructorId));
    }
}