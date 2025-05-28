package com.example.osid.domain.user.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.common.entity.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private Long id;

    @Column(nullable = false)
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
    private Role role;

}
