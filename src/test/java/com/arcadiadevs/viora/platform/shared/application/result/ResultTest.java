package com.arcadiadevs.viora.platform.shared.application.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {
    @Test
    void successResultExposesValue() {
        Result<String, String> result = Result.success("ok");

        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals("ok", result.success().orElseThrow());
        assertEquals("ok!", result.fold(value -> value + "!", String::valueOf));
    }

    @Test
    void failureResultExposesError() {
        Result<String, String> result = Result.failure("duplicate");

        assertTrue(result.isFailure());
        assertFalse(result.isSuccess());
        assertEquals("duplicate", result.failure().orElseThrow());
        assertEquals("duplicate!", result.fold(String::valueOf, error -> error + "!"));
    }
}

