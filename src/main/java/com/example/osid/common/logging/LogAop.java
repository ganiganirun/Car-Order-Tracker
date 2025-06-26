package com.example.osid.common.logging;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect // aop 기능을 갖음
@Component // bean 자동등록
public class LogAop {

	// pointcut

	// @Service 붙은 모든 클래스의 모든 메서드를 대상으로 함
	@Pointcut("within(@org.springframework.stereotype.Service *)")
	public void servicePointcut() {
	}

	// @RestController 붙은 모든 클래스의 모든 메서드를 대상으로 함
	@Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
	public void controllerPointcut() {
	}

	// @TransactionalEventListener 메서드 추적용
	@Pointcut("execution(* com.example.osid.domain.waitingorder.service..*(..))")
	public void eventListenerPointcut() {
	}

	// repository 하위 모든 클래스 메서드 실행 시간 측정
	@Pointcut("execution(* com.example.osid.domain..*.repository..*(..))")
	public void repositoryPointcut() {
	}

	// 배치 설정을 대상으로 함
	@Pointcut("execution(* com.example.osid.config.batch..*(..))")
	public void batchComponentPointcut() {
	}

	// 로깅 메서드

	// 컨트롤러 진입 로그
	@Before("controllerPointcut()")
	public void controllerLogMethodCall(JoinPoint joinPoint) {
		logMethodEntry("controller", joinPoint);
	}

	// 컨트롤러 리턴 로그
	@AfterReturning(value = "controllerPointcut()", returning = "returnObj")
	public void controllerLogMethodReturn(JoinPoint joinPoint, Object returnObj) {
		logMethodReturn("controller", joinPoint, returnObj);
	}

	// 서비스 진입 로그
	@Before("servicePointcut()")
	public void serviceLogMethodCall(JoinPoint joinPoint) {
		logMethodEntry("service", joinPoint);
	}

	// 서비스 리턴 로그 -> 서비스 메서드 정상 리턴 시 리턴값을 로그에 기록
	@AfterReturning(value = "servicePointcut()", returning = "returnObj")
	public void serviceLogMethodReturn(JoinPoint joinPoint, Object returnObj) {
		logMethodReturn("service", joinPoint, returnObj);
	}

	// 배치 관련 메서드 호출 로그 -> @Componet 기반 클래스의 메서드 진입 로그 단순 출력
	@Before("batchComponentPointcut()")
	public void batchLogMethodCall(JoinPoint joinPoint) {
		log.info("[batch] Method called: {}", joinPoint.getSignature());
	}

	// 이벤트 리스너 진입 로그
	@Before("eventListenerPointcut()")
	public void eventListenerLog(JoinPoint joinPoint) {
		logMethodEntry("event", joinPoint);
	}

	// 서비스/컨트롤러/이벤트 실행 시간 측정 -> 실행 전, 후 시간 측정해서 처리 시간 로깅
	@Around("servicePointcut() || controllerPointcut() || eventListenerPointcut()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return joinPoint.proceed();
		} finally {
			long end = System.currentTimeMillis();
			log.info("Executed {} in {}ms", joinPoint.getSignature(), end - start);
		}
	}

	// 레포지토리 실행 시간 측정
	@Around("repositoryPointcut()")
	public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		try {
			return joinPoint.proceed();
		} finally {
			long end = System.currentTimeMillis();
			log.debug("[Repository] {} executed in {}ms", joinPoint.getSignature(), end - start);
		}
	}

	// 예외 발생 경우 메서드 이름과 예외 메시지 로그
	@AfterThrowing(pointcut = "servicePointcut() || controllerPointcut() || batchComponentPointcut() || eventListenerPointcut()", throwing = "ex")
	public void logException(JoinPoint joinPoint, Throwable ex) {
		Method method = getMethod(joinPoint);
		log.error("❗ Exception in method: {} - {}", method.getName(), ex.getMessage(), ex);
	}

	private void logMethodEntry(String layer, JoinPoint joinPoint) {
		Method method = getMethod(joinPoint);
		log.info("===== [{}] Entering method: {} =====", layer, method.getName());
		logParameters(joinPoint);
	}

	private void logMethodReturn(String layer, JoinPoint joinPoint, Object returnObj) {
		Method method = getMethod(joinPoint);
		log.info("===== [{}] Returning method: {} =====", layer, method.getName());
		logReturnValue(returnObj);
	}

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
					args[i] != null ? args[i].getClass().getSimpleName() : "null",
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

