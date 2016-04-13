package com.mns.ops.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mns.ops.db.beans.Status;

@Component
@Aspect
public class StatusProcessor {
	public static final Logger LOGGER = LoggerFactory
			.getLogger(StatusProcessor.class);

	@Around("execution(* com.mns.ops.framework.mail.reader.BaseMailReader.saveStatus(..))")
	public void processStatus(ProceedingJoinPoint joinPoint) throws Throwable {
		Status status = (Status) joinPoint.getArgs()[0];
		if (null == status) {
			LOGGER.debug("Got null Status");
		}
		joinPoint.proceed();
	}
}