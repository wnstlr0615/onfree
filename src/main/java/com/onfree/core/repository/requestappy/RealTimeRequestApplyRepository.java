package com.onfree.core.repository.requestappy;

import com.onfree.core.entity.realtimerequset.RealTimeRequest;
import com.onfree.core.entity.requestapply.RealTimeRequestApply;
import com.onfree.core.entity.user.ArtistUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RealTimeRequestApplyRepository extends JpaRepository<RealTimeRequestApply, Long> {
    Long countByRealTimeRequestAndArtistUser(RealTimeRequest realTimeRequest, ArtistUser artistUser);
}
