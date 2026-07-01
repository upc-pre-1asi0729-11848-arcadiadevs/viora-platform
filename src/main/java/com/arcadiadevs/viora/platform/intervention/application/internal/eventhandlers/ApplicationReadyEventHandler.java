package com.arcadiadevs.viora.platform.intervention.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.intervention.application.commandservices.SpecialistSeedCommandService;
import com.arcadiadevs.viora.platform.intervention.domain.model.commands.SeedSpecialistsCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Seeds the specialist catalog once the application is ready.
 */
@Service("interventionApplicationReadyEventHandler")
@Slf4j
public class ApplicationReadyEventHandler {

    private final SpecialistSeedCommandService specialistSeedCommandService;

    public ApplicationReadyEventHandler(SpecialistSeedCommandService specialistSeedCommandService) {
        this.specialistSeedCommandService = specialistSeedCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        log.info("Verifying if specialist seeding is needed...");
        specialistSeedCommandService.handle(new SeedSpecialistsCommand());
        log.info("Specialist seeding verification finished.");
    }
}
