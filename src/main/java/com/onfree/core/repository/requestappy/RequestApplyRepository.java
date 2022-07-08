package com.onfree.core.repository.requestappy;

import com.onfree.core.entity.requestapply.RequestApply;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestApplyRepository extends JpaRepository<RequestApply, Long> {
    @EntityGraph(attributePaths = {"clientUser", "artistUser"})
    Optional<RequestApply> findByRequestApplyId(Long requestApplyId);

}
