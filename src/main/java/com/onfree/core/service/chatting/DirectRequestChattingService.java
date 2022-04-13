package com.onfree.core.service.chatting;

import com.onfree.common.error.code.RequestApplyErrorCode;
import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.RequestApplyException;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.entity.chatting.MessageChat;
import com.onfree.core.entity.requestapply.RequestApply;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.UserRepository;
import com.onfree.core.repository.chatting.ChattingRepository;
import com.onfree.core.repository.requestappy.RequestApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DirectRequestChattingService {
    private final ChattingRepository chattingRepository;
    private final UserRepository userRepository;
    private final RequestApplyRepository requestApplyRepository;

    @Transactional
    public void addChatting(Long applyId, User sender, Long receiverId, String message) {
        RequestApply requestApply = requestApplyRepository.findByRequestApplyId(applyId)
                .orElseThrow(() -> new RequestApplyException(RequestApplyErrorCode.NOT_FOUND_REQUEST_APPLY_ID));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));

        MessageChat messageChat = MessageChat.builder()
                .requestApply(requestApply)
                .sender(sender)
                .receiver(receiver)
                .message(message)
                .build();

        chattingRepository.save(messageChat);
    }
}
