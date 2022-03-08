package com.onfree.core.entity.requestapply;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "REAL")
public class RealTimeRequestApply extends RequestApply{
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "real_time_request_id")
    private RealTimeRequest realTimeRequest;

    @Builder
    public RealTimeRequestApply(User user, ArtistUser artistUser, RequestApplyStatus status, RealTimeRequest realTimeRequest) {
        super(user, artistUser, status);
        this.realTimeRequest = realTimeRequest;
    }

    public static RealTimeRequestApply createRealTimeRequestApply(RealTimeRequest realTimeRequest, ArtistUser artistUser, RequestApplyStatus status) {
        return RealTimeRequestApply.builder()
                .realTimeRequest(realTimeRequest)
                .user(realTimeRequest.getUser())
                .artistUser(artistUser)
                .status(status)
                .build();
    }
}
