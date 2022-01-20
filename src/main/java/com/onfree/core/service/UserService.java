package com.onfree.core.service;

import com.onfree.common.error.code.UserErrorCode;
import com.onfree.common.error.exception.UserException;
import com.onfree.core.dto.UpdateUserNotificationDto;
import com.onfree.core.entity.user.User;
import com.onfree.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    /** 사용자 알림 설정 */
    @Transactional
    public void updateUserNotification(Long userId, UpdateUserNotificationDto updateUserNotificationDto) {
        final User user = getUser(userId);
        user.updateNotification(
                updateUserNotificationDto.toEntity()
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND_USERID));
    }
}
