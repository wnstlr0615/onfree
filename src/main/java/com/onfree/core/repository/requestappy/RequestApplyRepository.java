package com.onfree.core.repository.requestappy;

import com.onfree.core.entity.requestapply.RequestApply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestApplyRepository extends JpaRepository<RequestApply, Long> {
}
