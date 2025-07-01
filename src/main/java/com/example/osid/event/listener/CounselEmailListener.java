package com.example.osid.event.listener;

import com.example.osid.config.RabbitMQConfig;
import com.example.osid.domain.counsel.entity.Counsel;
import com.example.osid.domain.counsel.repository.CounselRepository;
import com.example.osid.domain.email.service.EmailService;
import com.example.osid.event.CounselApplicationEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mq.enabled", havingValue = "true", matchIfMissing = false)
public class CounselEmailListener {

    private final CounselRepository counselRepository;
    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.COUNSEL_QUEUE)
    public void handleCounselNotification(CounselApplicationEvent event) {
        log.info("상담 이메일 수신: 상담 ID={}, 딜러 ID={}", event.getCounselId(), event.getDealerId());

        try {
            Counsel counsel = counselRepository.findWithDealerAndUserById(event.getCounselId())
                    .orElseThrow(() -> new CounselException(CounselErrorCode.COUNSEL_NOT_FOUND));

            emailService.sendCounselNotificationToDealer(counsel);

            log.info("상담 이메일 발송 완료: 상담 ID={}, 딜러 이메일={}", event.getCounselId(), event.getDealerEmail());
        } catch (Exception e) {
            log.error("상담 이메일 발송 실패: 상담 ID={}", event.getCounselId(), e);
            throw e; // DLQ로 넘기기 위해 예외 전파
        }
    }
}
