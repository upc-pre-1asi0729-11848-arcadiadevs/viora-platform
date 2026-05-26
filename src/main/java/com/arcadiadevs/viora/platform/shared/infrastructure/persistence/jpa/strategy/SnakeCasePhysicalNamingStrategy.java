package com.arcadiadevs.viora.platform.shared.infrastructure.persistence.jpa.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;

public class SnakeCasePhysicalNamingStrategy implements PhysicalNamingStrategy{

    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(this.toPlural(identifier));
    }


}
