package com.example.osid.domain.counsel.entity;

import com.example.osid.common.entity.BaseEntity;
import com.example.osid.domain.counsel.enums.CounselStatus;
import com.example.osid.domain.dealer.entity.Dealer;
import com.example.osid.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "counsel")
public class Counsel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;

    private String title;

    @Column(name = "counsel_status", length = 30)
    @Enumerated(EnumType.STRING)
    private CounselStatus counselStatus;

    private String userContent;

    @Column(columnDefinition = "TEXT")
    private String dealerMemo;

    public Counsel(User user, Dealer dealer, String title, String userContent, CounselStatus status) {
        this.user = user;
        this.dealer = dealer;
        this.title = title;
        this.userContent = userContent;
        this.counselStatus = status;
    }

    public void writeMemo(String dealerMemo) {
        this.dealerMemo = dealerMemo;
    }

    public void updateMemo(String dealerMemo) {
        this.dealerMemo = dealerMemo;
    }
}
