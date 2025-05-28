package com.example.osid.domain.history.entity;

import com.example.osid.domain.history.enums.ProductionHistory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "histories")
public class History {

    @Id
    private Long id;

    @Column(nullable = false)
    private String bodyNumber; //

    @Column(nullable = false)
    private ProductionHistory productionHistory;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime startDate; // 공정 시작 날짜 시간

    @Column
    private LocalDateTime endDate; // 공정 끝난 날짜 시간 이 친구 null  가능

    @Column(nullable = false)
    private String productionPlant; //생산 공장

    @Column
    private String issue; //예외 이슈(비고)

}
