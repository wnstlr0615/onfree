package com.onfree.core.service;

import com.onfree.core.entity.JWTRefreshToken;
import com.onfree.core.repository.JWTRefreshTokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JWTRefreshTokenServiceTest {
    @Mock
    JWTRefreshTokenRepository jwtRefreshTokenRepository;
    @InjectMocks
    JWTRefreshTokenService jwtRefreshTokenService;
    @Captor
    ArgumentCaptor<JWTRefreshToken> tokenArgumentCaptor;

    @Test
    @DisplayName("기존 토큰이 있을 경우 새로운 토큰으로 업데이트")
    public void givenUsernameAndRFToken_whenUpdateOrSaveRefreshToken_ThenEntityUpdate() throws Exception{
        //given
        final String username = "joon";
        final String oldRefreshToken = "asdgfasdfas.asdfadfas.asdfasdfas";
        final String newRefreshToken = "aaaaaaaaa.bbbbbbbb.ccccccc";

        when(jwtRefreshTokenRepository.findById(anyString()))
                .thenReturn(
                    Optional.ofNullable(
                            createJWTRefreshToken(username, oldRefreshToken)
                    )
        );
        //when
        jwtRefreshTokenService.updateOrSaveRefreshToken(username, newRefreshToken);
        ArgumentCaptor<JWTRefreshToken> jwtRefreshTokenArgumentCaptor = ArgumentCaptor.forClass(JWTRefreshToken.class);
        //then
        verify(jwtRefreshTokenRepository, never()).save(any(JWTRefreshToken.class));
    }

    private JWTRefreshToken createJWTRefreshToken(String username, String refreshToken) {
        return JWTRefreshToken.builder()
                .userName(username)
                .refreshToken(refreshToken)
                .build();
    }

    @Test
    @DisplayName("기존 토큰이 없을 경우 새로운 데이터 저장")
    public void givenUsernameAndRFToken_whenFindByIdEmpty_ThenEntitySave() throws Exception{
        //given
        final String username = "joon";
        final String oldRefreshToken = "asdgfasdfas.asdfadfas.asdfasdfas";

        when(jwtRefreshTokenRepository.findById(anyString()))
                .thenReturn(
                        Optional.empty()
                );
        //when
        jwtRefreshTokenService.updateOrSaveRefreshToken(username, oldRefreshToken);
        //then
        verify(jwtRefreshTokenRepository).save(any(JWTRefreshToken.class));
    }

    @Test
    @DisplayName("사용자 아이디로된 토큰이 있을 경우 제거")
    public void givenUsername_whenDeleteToken_thenSuccessDelete() throws Exception{
        //given
        final String username = "joon@naver.com";
        when(jwtRefreshTokenRepository.countByUserName(anyString()))
                .thenReturn(1);

        //when
        jwtRefreshTokenService.deleteTokenByUsername(username);

        //then
        verify(jwtRefreshTokenRepository).countByUserName(anyString());
        verify(jwtRefreshTokenRepository).deleteById(anyString());
    }

    @Test
    @DisplayName("사용자 아이디로된 토큰이 없을 경우 생략")
    public void givenUsername_whenDeleteTokenButIsNotData_thenNotWorking() throws Exception{
        //given
        final String username = "joon@naver.com";
        when(jwtRefreshTokenRepository.countByUserName(username))
                .thenReturn(0);
        //when
        jwtRefreshTokenService.deleteTokenByUsername(username);

        //then
        verify(jwtRefreshTokenRepository).countByUserName(anyString());
        verify(jwtRefreshTokenRepository, never()).deleteById(anyString());
    }

}