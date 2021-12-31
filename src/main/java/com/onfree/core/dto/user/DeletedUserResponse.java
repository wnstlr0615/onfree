package com.onfree.core.dto.user;

import com.onfree.core.entity.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletedUserResponse {
    private Long userId;
    private boolean deleted;

    public static DeletedUserResponse fromEntity(User entity){
        return DeletedUserResponse
                .builder()
                .userId(entity.getUserId())
                .deleted(entity.getDeleted())
                .build() ;
    }
}
