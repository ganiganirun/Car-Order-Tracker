package com.example.osid.domain.learning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingScheduler {

    private final TrainingService trainingService;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void scheduleTrainingAndUpload() {
        System.out.println("[스케줄러] 주기적 학습 및 업로드 시작");
        trainingService.triggerTrainingAndUpload();
    }
}
