package com.pandaer.maker.meta;

/**
 * 校验元信息异常
 */
public class ValidatedMetaException extends RuntimeException {

    public ValidatedMetaException(String message) {
        super(message);
    }

    public ValidatedMetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
