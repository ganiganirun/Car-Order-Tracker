package com.example.osid.domain.dealer.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.dealer.dto.request.DealerUpdatedRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "dealers")
@AllArgsConstructor
@NoArgsConstructor
public class Dealer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; //이메일

    @Column(nullable = false)
    private String password; //비밀번호

    @Column(nullable = false)
    private String name; //이름

    private String point = "미배정"; //지점

    @Column(nullable = false)
    private String phoneNumber; //전화번호

    private Role role = Role.DEALER; //역할

    public Dealer(
        String email,
        String password,
        String name,
        String phoneNumber
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void UpdateDelaer(DealerUpdatedRequestDto dealerUpdatedRequestDto) {
        if (dealerUpdatedRequestDto.getName() != null) {
            this.name = dealerUpdatedRequestDto.getName();
        }
        if (dealerUpdatedRequestDto.getPhoneNumber() != null) {
            this.phoneNumber = dealerUpdatedRequestDto.getPhoneNumber();
        }
    }
}
