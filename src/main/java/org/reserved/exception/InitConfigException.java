package org.reserved.exception;

import org.springframework.beans.BeansException;

/**
 * @author chenxuning
 */
public class InitConfigException extends BeansException {
    public InitConfigException(String msg) {
        super(msg);
    }

    public InitConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
