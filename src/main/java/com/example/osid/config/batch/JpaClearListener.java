package com.example.osid.config.batch;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@StepScope   // 이걸 달면 EMF도 스텝 실행 시점에 프로시(proxy)로 주입됩니다.
public class JpaClearListener implements ChunkListener {

	private final EntityManagerFactory emf;

	@Override
	public void afterChunk(ChunkContext context) {
		emf.createEntityManager().clear();   // 힙 누수 방지
	}
}
