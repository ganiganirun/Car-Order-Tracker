package com.example.osid.domain.user.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import com.example.osid.domain.user.dto.request.UserUpdatedRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; //이메일

    @Column(nullable = false)
    private String password; //비밀번호

    @Column(nullable = false)
    private String name; //이름

    @Column(nullable = false)
    private LocalDate dateOfBirth; //생년월일

    @Column(nullable = false)
    private String phoneNumber; //전화번호

    @Column(nullable = false)
    private String address; //주소

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    public User(
        String email,
        String password,
        String name,
        LocalDate dateOfBirth,
        String phoneNumber,
        String address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void UpdatedUsere(UserUpdatedRequestDto userUpdatedRequestDto) {
        if (userUpdatedRequestDto.getName() != null) {
            this.name = userUpdatedRequestDto.getName();
        }
        if (userUpdatedRequestDto.getPhoneNumber() != null) {
            this.phoneNumber = userUpdatedRequestDto.getPhoneNumber();
        }
        if (userUpdatedRequestDto.getAddress() != null) {
            this.address = userUpdatedRequestDto.getAddress();
        }
    }
}
