package com.arcadiadevs.viora.platform.intervention.interfaces.rest.resources;

/**
 * Private specialist contact details, only released once the related proposal has
 * been accepted.
 */
public record SpecialistContactResource(
        Long specialistId,
        String fullName,
        String phone,
        String email,
        String whatsapp
) {
}
