package com.onfree.validator;

import com.onfree.core.dto.user.artist.status.StatusMarkDto;
import com.onfree.core.entity.user.StatusMark;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class StatusMarkValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return  clazz.isAssignableFrom(StatusMarkDto.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final StatusMarkDto statusMarkDto = (StatusMarkDto) target;
        final String statusMark = statusMarkDto.getStatusMark();
        if(isNotStatusMark(statusMark)){
            errors.rejectValue("statusMark", "typeMissMatch.statusMark", "입력된 statusMark 가 올바르지 않습니다.");
        }
    }

    private boolean isNotStatusMark(String statusMark) {
        try {
            StatusMark.valueOf(statusMark);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }
}
