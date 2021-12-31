package com.onfree.core.entity.user;

import lombok.Getter;

@Getter
public enum Role {
    NORMAL, ARTIST;

    public String getValue(){
        return "ROLE_"+this.name();
    }
}
