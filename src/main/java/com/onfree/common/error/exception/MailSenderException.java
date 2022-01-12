package com.onfree.common.error.exception;

import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.MailErrorCode;
import lombok.Getter;

@Getter

public class MailSenderException extends RuntimeException{
    private ErrorCode errorCode;
    private String errorMessage;

    public MailSenderException(MailErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public MailSenderException(MailErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
