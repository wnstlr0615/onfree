package com.onfree.common.aop;

import com.onfree.common.error.code.GlobalErrorCode;
import com.onfree.common.error.response.FieldErrorDto;
import com.onfree.common.error.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;


@Aspect
@Component
@Slf4j
public class ValidateAop {
    @Pointcut("execution(* com.onfree.controller..create*(..))")
    public void isCreate(){}

    @Pointcut("execution(* com.onfree.controller..update*(..))")
    public void isUpdate(){}

    @Around("isCreate() || isUpdate()")
    public Object validatedRequestBody(ProceedingJoinPoint joinPoint) throws Throwable {
        for(Object o : joinPoint.getArgs()){
            if(o instanceof BindingResult){
                BindingResult error = (BindingResult)o;
                if(error.hasErrors()){
                    printFiledLog(error, joinPoint);
                    final List<FieldErrorDto> fieldErrorDtos = getFieldErrorDtos(error);
                    throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST, fieldErrorDtos);
                }
            }
        }
        return joinPoint.proceed();
    }

    private void printFiledLog(BindingResult errors, ProceedingJoinPoint joinPoint) {
        final String methodName = joinPoint.getSignature().getName();
       errors.getFieldErrors()
               .forEach(fieldError ->
                        log.error("methodName : {}, field :{} ,rejectValue : {} , message : {}"
                                ,methodName, fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage()
                        )
               );
    }
    private List<FieldErrorDto> getFieldErrorDtos(BindingResult errors) {
        return errors.getFieldErrors().stream()
                .map(FieldErrorDto::fromFieldError)
                .collect(Collectors.toList());
    }


}
