package com.onfree.common.error.exception;


import com.onfree.common.error.code.CustomerCenterErrorCode;
import com.onfree.common.error.code.ErrorCode;
import com.onfree.common.error.code.FileErrorCode;
import lombok.Getter;


@Getter
public class FileException extends RuntimeException {
    ErrorCode errorCode;
    String errorMessage;

    public FileException(FileErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }

    public FileException(ErrorCode errorCode, String errorMessage) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
