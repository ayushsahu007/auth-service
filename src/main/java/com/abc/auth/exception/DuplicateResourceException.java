package com.abc.auth.exception;

import java.util.Map;

public class DuplicateResourceException extends RuntimeException {

    private final Map<String, String> errors;

    public DuplicateResourceException(Map<String, String> errors) {
        super("Duplicate resources found.");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
