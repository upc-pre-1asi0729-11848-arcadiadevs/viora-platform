package com.arcadiadevs.viora.platform.intervention.application.commandservices;

import com.arcadiadevs.viora.platform.intervention.domain.model.aggregates.Specialist;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SeedSpecialistsCommand;
import com.arcadiadevs.viora.platform.intervention.domain.model.valueobjects.SpecialistAvailability;
import com.arcadiadevs.viora.platform.intervention.domain.repositories.SpecialistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Seeds the default specialist catalog so recommendations and the case flow work
 * end-to-end without a specialist-facing application. Idempotent: specialists are
 * only inserted when absent (matched by full name).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialistSeedCommandService {

    private final SpecialistRepository specialistRepository;

    private static final List<Specialist> DEFAULT_SPECIALISTS = List.of(
            new Specialist(
                    "Valeria Rojas", "Phytosanitary specialist", 96.0, 24, 6.4,
                    List.of("Xylella monitoring", "Biological stress", "Field inspection"),
                    SpecialistAvailability.AVAILABLE_TODAY,
                    "+51 987 654 321", "valeria.rojas@viora.example", "+51 987 654 321"),
            new Specialist(
                    "Marco Salcedo", "Agricultural pest technician", 92.0, 17, 9.1,
                    List.of("Leaf symptoms", "Low-vigor inspection", "Treatment follow-up"),
                    SpecialistAvailability.AVAILABLE_TOMORROW,
                    "+51 987 000 111", "marco.salcedo@viora.example", "+51 987 000 111"),
            new Specialist(
                    "Camila Torres", "Crop health consultant", 94.0, 31, 12.8,
                    List.of("Olive disease assessment", "Technical prescriptions"),
                    SpecialistAvailability.AVAILABLE_THIS_WEEK,
                    "+51 987 222 333", "camila.torres@viora.example", "+51 987 222 333")
    );

    @Transactional
    public void handle(SeedSpecialistsCommand command) {
        log.info("Starting specialist seeding process...");
        for (Specialist specialist : DEFAULT_SPECIALISTS) {
            if (!specialistRepository.existsByFullName(specialist.getFullName())) {
                specialistRepository.save(specialist);
                log.info("Seeded specialist: {}", specialist.getFullName());
            }
        }
        log.info("Specialist seeding process completed.");
    }
}
