package com.example.osid.common.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAop {

	// @Service 붙은 모든 클래스
	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void servicePointcut() {
	}

	// @RestController 붙은 모든 클래스
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerPointcut() {
	}

	@Before("servicePointcut()")
	public void serviceLogMethodCall(JoinPoint joinPoint) {
		Method method = getMethod(joinPoint);
		// 메소드 진입 시 메소드 이름을 로깅
		log.info("===== [service] Entering method: {} =====", method.getName());
		// 메소드 파라미터를 로깅
		logParameters(joinPoint);
	}

	// AfterReturning 어드바이스: 메소드가 값을 반환한 후 실행되며, 지정된 Pointcut에 해당하는 메소드에서 작동
	@AfterReturning(value = "servicePointcut()", returning = "returnObj")
	public void serviceLogMethodReturn(JoinPoint joinPoint, Object returnObj) {
		Method method = getMethod(joinPoint);
		// 메소드 반환 시 메소드 이름을 로깅
		log.info("===== [service] Returning method: {} =====", method.getName());
		// 반환된 값의 타입과 값을 로깅
		logReturnValue(returnObj);
	}

	@Before("controllerPointcut()")
	public void controllerLogMethodCall(JoinPoint joinPoint) {
		Method method = getMethod(joinPoint);
		// 메소드 진입 시 메소드 이름을 로깅
		log.info("===== [controller] Entering method: {} =====", method.getName());
		// 메소드 파라미터를 로깅
		logParameters(joinPoint);
	}

	// // AfterReturning 어드바이스: 메소드가 값을 반환한 후 실행되며, 지정된 Pointcut에 해당하는 메소드에서 작동
	// @AfterReturning(value = "controllerPointcut()", returning = "returnObj")
	// public void controllerLogMethodReturn(JoinPoint joinPoint, Object returnObj) {
	// 	Method method = getMethod(joinPoint);
	// 	// 메소드 반환 시 메소드 이름을 로깅
	// 	log.info("===== [controller] Returning method: {} =====", method.getName());
	// 	// 반환된 값의 타입과 값을 로깅
	// 	logReturnValue(returnObj);
	// }

	// JoinPoint에서 메소드 정보를 추출하는 메소드
	private Method getMethod(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		return signature.getMethod();
	}

	// 메소드 파라미터를 로깅하는 메소드
	private void logParameters(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

		if (args.length == 0) {
			log.info("No parameters");
		} else {
			for (int i = 0; i < args.length; i++) {
				log.info("Parameter name: {}, type: {}, value: {}",
					parameterNames[i],
					args[i].getClass().getSimpleName(),
					args[i]);
			}
		}
	}

	// 반환된 객체를 로깅하는 메소드
	private void logReturnValue(Object returnObj) {
		if (returnObj != null) {
			log.info("Return type: {}, value: {}", returnObj.getClass().getSimpleName(), returnObj);
		} else {
			log.info("Return value is null");
		}
	}
}

