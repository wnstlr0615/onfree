package com.onfree.config.security;

import com.onfree.core.entity.user.User;
import com.onfree.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User userEntity = getUserFindByUsername(username);
        return new CustomUserDetail(userEntity);
    }

    private User getUserFindByUsername(String username) {
        return userRepository.findByEmailAndDeletedIsFalse(username)
                        .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다."));
    }
}
