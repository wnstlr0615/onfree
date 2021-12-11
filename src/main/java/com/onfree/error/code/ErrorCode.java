package com.onfree.error.code;

import lombok.Getter;


public interface ErrorCode {
     String getDescription();
     int getStatus();
}
