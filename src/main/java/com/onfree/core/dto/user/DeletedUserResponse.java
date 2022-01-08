package com.onfree.core.dto.user;

import com.onfree.core.entity.user.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeletedUserResponse {
    @ApiModelProperty(value = "사용자 PK", example = "1")
    private Long userId;
    @ApiModelProperty(value = "삭제 처리", example = "true")
    private boolean deleted;

    public static DeletedUserResponse fromEntity(User entity){
        return DeletedUserResponse
                .builder()
                .userId(entity.getUserId())
                .deleted(entity.getDeleted())
                .build() ;
    }
}
