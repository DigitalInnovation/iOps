package org.ht.iops.aop;

import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Profiler {
	public static final Logger LOGGER = LoggerFactory.getLogger(Profiler.class);

	@Around("execution(* org.ht.iops.framework.mail.reciever..*.*(..))")
	public Object profileMailReciever(ProceedingJoinPoint joinPoint)
			throws Throwable {
		final Instant start = Instant.now();
		Object result = joinPoint.proceed();
		final Instant end = Instant.now();
		LOGGER.debug("Mail reciever service took "
				+ Duration.between(start, end) + " seconds to complete.");
		return result;
	}
}
