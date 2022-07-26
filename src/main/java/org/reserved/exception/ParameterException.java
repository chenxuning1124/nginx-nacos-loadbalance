package org.reserved.exception;

import org.springframework.beans.BeansException;

/**
 * @author chenxuning
 */
public class ParameterException extends BeansException {
    public ParameterException(String msg) {
        super(msg);
    }

    public ParameterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
