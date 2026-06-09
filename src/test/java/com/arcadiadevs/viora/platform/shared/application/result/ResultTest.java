package com.arcadiadevs.viora.platform.shared.application.result;

import java.util.Optional;
import java.util.function.Function;

class Result<T, E> {
    private final T value;
    private final E error;
    private final boolean isSuccess;

    private Result(T value, E error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }

    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }

    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public Optional<T> success() {
        return Optional.ofNullable(value);
    }

    public Optional<E> failure() {
        return Optional.ofNullable(error);
    }

    public <R> R fold(Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
        if (this.isSuccess) {
            return onSuccess.apply(value);
        } else {
            return onFailure.apply(error);
        }
    }
}