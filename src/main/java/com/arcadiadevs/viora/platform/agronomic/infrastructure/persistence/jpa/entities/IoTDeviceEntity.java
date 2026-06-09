package com.arcadiadevs.viora.platform.agronomic.infrastructure.persistence.jpa.entities;

import com.arcadiadevs.viora.platform.agronomic.domain.model.valueobjects.IoTDeviceStatus;
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
}
