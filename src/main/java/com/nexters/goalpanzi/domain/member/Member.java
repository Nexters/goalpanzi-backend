package com.nexters.goalpanzi.domain.member;

import com.nexters.goalpanzi.domain.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname")
    private String nickname;

}
