package com.agileboot.infrastructure.aspectj;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * @author valarchie
 */
@Aspect
@Component
@Slf4j
public class MethodLogAspect {

    @Pointcut("execution(public * com.agileboot.orm.service.*(..))")
    public void ormServiceLog() {
    }

    @Around("ormServiceLog()")
    public Object aroundOrmService(ProceedingJoinPoint joinPoint)throws Throwable{

        Object proceed = joinPoint.proceed();
        log.info("ORM SERVICE : {} ; REQUEST：{} ; RESPONSE : {}",
            joinPoint.getSignature().toShortString(),
            JSONUtil.toJsonStr(joinPoint.getArgs()),
            JSONUtil.toJsonStr(proceed));
        return proceed;
    }

    @Pointcut("execution(public * com.agileboot.domain.*.*service.*(..))")
    public void domainServiceLog() {
    }

    @Around("domainServiceLog()")
    public Object aroundDomainService(ProceedingJoinPoint joinPoint)throws Throwable{

        Object proceed = joinPoint.proceed();
        log.info("ORM SERVICE : {} ; REQUEST：{} ; RESPONSE : {}",
            joinPoint.getSignature().toShortString(),
            JSONUtil.toJsonStr(joinPoint.getArgs()),
            JSONUtil.toJsonStr(proceed));
        return proceed;
    }


}
