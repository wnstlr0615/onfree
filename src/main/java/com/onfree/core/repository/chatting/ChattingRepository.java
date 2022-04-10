package com.onfree.core.repository.chatting;

import com.onfree.core.entity.chatting.Chatting;
import com.onfree.core.entity.requestapply.RequestApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
    List<Chatting> findAllByRequestApply(RequestApply requestApply);
}
