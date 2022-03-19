package com.onfree.core.entity.requestapply;

import com.onfree.common.model.BaseTimeEntity;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class RequestApply extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestApplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_user_id")
    private User clientUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_user_id")
    private ArtistUser artistUser;

    @Enumerated(EnumType.STRING)
    private RequestApplyStatus status;

    public RequestApply(User clientUser, ArtistUser artistUser, RequestApplyStatus status) {
        this.clientUser = clientUser;
        this.artistUser = artistUser;
        this.status = status;
    }

    public void updateStatus(RequestApplyStatus status) {
        this.status =status;
    }
}