package com.mns.ops.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AOPLogger {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(AOPLogger.class);

	@Around("execution(* com.mns.ops.rest..*.*(..))")
	public Object restLogger(ProceedingJoinPoint joinPoint) throws Throwable {
		String methodName = joinPoint.getTarget().getClass().getName() + ":"
				+ joinPoint.getSignature().getName();
		LOGGER.debug("Starting method execution " + methodName);
		Object result = joinPoint.proceed();
		LOGGER.debug("Ending method execution " + methodName);
		return result;
	}
}
