package com.ssafy.nanumi.db.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="si", columnDefinition="VARCHAR(20)", nullable = false)
    private String si;

    @Column(name="gugun", columnDefinition="VARCHAR(20)", nullable = false)
    private String guGun;

    @Column(name="dong", columnDefinition="VARCHAR(20)", nullable = false)
    private String dong;
}