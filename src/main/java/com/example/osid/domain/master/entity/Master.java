package com.example.osid.domain.master.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.master.dto.request.MasterUpdatedRequestDto;

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
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "masters")
public class Master extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessNumber; //사업자 번호

    @Column(nullable = false)
    private String name; // 담당자 이름

    @Column(nullable = false)
    private String phoneNumber; //전화번호

    @Column(nullable = false)
    private String email; //이메일

    @Column(nullable = false)
    private String password; //비밀번호

    @Column(nullable = false)
    private String address; //주소

    @Column(nullable = false)
    private String license; //라이센스

    @Column(nullable = false)
    private Role role = Role.MASTER; //역할

    public Master(
        String businessNumber,
        String name,
        String phoneNumber,
        String email,
        String password,
        String address,
        String license
    ) {
        this.businessNumber = businessNumber;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.address = address;
        this.license = license;
    }

    public void UpdateMaster(MasterUpdatedRequestDto masterUpdatedRequestDto) {
        if (masterUpdatedRequestDto.getName() != null) {
            this.name = masterUpdatedRequestDto.getName();
        }
        if (masterUpdatedRequestDto.getPhoneNumber() != null) {
            this.phoneNumber = masterUpdatedRequestDto.getPhoneNumber();
        }
        if (masterUpdatedRequestDto.getAddress() != null) {
            this.address = masterUpdatedRequestDto.getAddress();
        }
    }
}
