package com.example.osid.domain.option.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.option.enums.OptionCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "options")
public class Option extends BaseEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name; //옵션 이름

    @Column(nullable = false)
    private String description; //옵션 설명

    @Column(nullable = false)
    private String image; //옵션 이미지

    @Column(nullable = false)
    private OptionCategory category; //옵션 카테고리

    @Column(nullable = false)
    private Long price; //옵션 가격

}
