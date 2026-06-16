package com.arcadiadevs.viora.platform.surveillance.application.internal.eventhandlers;

import com.arcadiadevs.viora.platform.surveillance.application.commandservices.SymptomCommandService;
import com.arcadiadevs.viora.platform.surveillance.domain.model.commands.SeedSymptomsCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service("surveillanceApplicationReadyEventHandler")
@Slf4j
public class ApplicationReadyEventHandler {

    private final SymptomCommandService symptomCommandService;

    public ApplicationReadyEventHandler(SymptomCommandService symptomCommandService) {
        this.symptomCommandService = symptomCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        log.info("Starting to verify if symptom seeding is needed for {} at {}", applicationName, currentTimestamp());
        
        var seedSymptomsCommand = new SeedSymptomsCommand();
        symptomCommandService.handle(seedSymptomsCommand);
        
        log.info("Symptom seeding verification finished for {} at {}", applicationName, currentTimestamp());
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
