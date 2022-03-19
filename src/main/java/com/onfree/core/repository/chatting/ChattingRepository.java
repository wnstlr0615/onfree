package com.onfree.core.repository.chatting;

import com.onfree.core.entity.chatting.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRepository extends JpaRepository<Chatting, Long> {
}
