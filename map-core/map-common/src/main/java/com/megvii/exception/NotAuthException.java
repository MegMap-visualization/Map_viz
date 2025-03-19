package com.megvii.exception;

public class NotAuthException extends RuntimeException {
    public NotAuthException(String msg) {
        super(msg);
    }
}
