package com.onfree.validator;

import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import static org.assertj.core.api.Assertions.assertThat;

class StatusMarkValidatorTest {
    StatusMarkValidator statusMarkValidator = new StatusMarkValidator();
    @Test
    @DisplayName("[성공] StatusMarkValidator 검증 테스트")
    public void success() throws Exception{
        //given
        final StatusMarkDto target = givenStatusMarkDto("OPEN");
        Errors errors = new BeanPropertyBindingResult(target, "statusMarkDto");
        //when
        statusMarkValidator.validate(target, errors);
        //then
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    @DisplayName("[실패] StatusMarkValidator 검증 테스트 - 다른 값 입력")
    public void fail() throws Exception{
        //given
        final StatusMarkDto target = givenStatusMarkDto("OPCLO");
        Errors errors = new BeanPropertyBindingResult(target, "statusMarkDto");
        //when
        statusMarkValidator.validate(target, errors);
        //then
        assertThat(errors.hasErrors()).isTrue();
    }

    private StatusMarkDto givenStatusMarkDto(String statusMark) {
        return StatusMarkDto.builder()
                .statusMark(statusMark)
                .build();
    }

}