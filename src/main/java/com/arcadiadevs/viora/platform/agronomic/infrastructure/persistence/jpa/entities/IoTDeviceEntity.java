package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceType;
import com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for IoTDevice persistence.
 * Table: iot_devices (snake_case via SnakeCasePhysicalNamingStrategy)
 */
@Entity
@Table(name = "iot_devices")
@Getter
@Setter
@NoArgsConstructor
public class IoTDeviceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false, length = 150)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IoTDeviceStatus status;

    /**
     * Activation/claim code (also the device serial). Nullable so an
     * {@code ddl-auto=update} adds the column to any pre-existing rows without a
     * migration; unique so a physical unit can only be claimed once.
     */
    @Column(unique = true, length = 20)
    private String activationCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private IoTDeviceType deviceType;
}
