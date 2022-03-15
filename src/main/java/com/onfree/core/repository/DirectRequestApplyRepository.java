package com.onfree.core.repository;

import com.onfree.core.entity.requestapply.DirectRequestApply;
import com.onfree.core.entity.user.ArtistUser;
import com.onfree.core.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectRequestApplyRepository extends JpaRepository<DirectRequestApply, Long> {
    List<DirectRequestApply> findAllByClientUserAndArtistUser(User user, ArtistUser artistUser);
}
