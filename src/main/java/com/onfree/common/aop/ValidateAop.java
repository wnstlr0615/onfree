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
    //삭제 예정 ( *Add 로 메소드명 변경)
    @Pointcut("execution(* com.onfree.controller..create*(..))")
    public void isCreate(){}

    @Pointcut("execution(* com.onfree.controller..update*(..))")
    public void isUpdatePrefix(){}

    @Pointcut("execution(* com.onfree.controller..*Update(..))")
    public void isUpdateSuffix(){}

    @Pointcut("execution(* com.onfree.controller..*Add(..))")
    public void isAdd(){}



    @Around("isCreate() || isUpdatePrefix() || isAdd() ||  isUpdateSuffix()")
    public Object validatedRequestBody(ProceedingJoinPoint joinPoint) throws Throwable {
        for(Object o : joinPoint.getArgs()){
            if(o instanceof BindingResult){
                BindingResult error = (BindingResult)o;
                if(error.hasErrors()){
                    printFiledLog(error, joinPoint);
                    throw new GlobalException(GlobalErrorCode.NOT_VALIDATED_REQUEST, error.getFieldErrors());
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



}
