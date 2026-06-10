package com.arcadiadevs.viora.platform.shared.application.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    @Test
    void exposesSuccessfulValue() {
        Result<String, ApplicationError> result = Result.success("ok");

        assertTrue(result.isSuccess());
        assertEquals("ok", result.success().orElseThrow());
    }

    @Test
    void exposesFailureValue() {
        var error = ApplicationError.notFound("plot", "1");
        Result<String, ApplicationError> result = Result.failure(error);

        assertTrue(result.isFailure());
        assertEquals(error, result.failure().orElseThrow());
    }

    @Test
    void foldsBothResultBranches() {
        Result<Integer, String> success = Result.success(4);
        Result<Integer, String> failure = Result.failure("failed");

        assertEquals("value:4", success.fold(
                value -> "value:" + value,
                error -> "error:" + error
        ));
        assertEquals("error:failed", failure.fold(
                value -> "value:" + value,
                error -> "error:" + error
        ));
    }
}
