package com.onfree.core.repository.chatting;

import com.onfree.core.entity.chatting.EstimateSheetChat;
import com.onfree.core.entity.requestapply.RequestApply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstimateSheetChatRepository extends JpaRepository<EstimateSheetChat, Long> {
    Optional<EstimateSheetChat> findByOrderId(String orderId);

    Optional<EstimateSheetChat> findByOrderIdAndEstimatedAmount(String orderId, Long estimatedAmount);

    Optional<EstimateSheetChat> findByRequestApplyAndOrderedIsTrue(RequestApply requestApply);
}
