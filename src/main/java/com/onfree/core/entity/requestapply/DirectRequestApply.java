package com.onfree.core.entity.requestapply;

import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue(value = "DIRECT")
public class DirectRequestApply extends RequestApply{
    @Builder
    public DirectRequestApply(User clientUser, ArtistUser artistUser, RequestApplyStatus status) {
        super(clientUser, artistUser, status);
    }

    //== 생성 메서드 ==//
    public static DirectRequestApply createDirectRequestApply(User user, ArtistUser artistUser, RequestApplyStatus status) {
        return DirectRequestApply.builder()
                .clientUser(user)
                .artistUser(artistUser)
                .status(status)
                .build();
    }
}
