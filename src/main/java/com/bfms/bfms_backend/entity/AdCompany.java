package com.bfms.bfms_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ad_company")
@Getter
@Setter
@NoArgsConstructor
public class AdCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "tax_code", unique = true, nullable = false)
    private String taxCode;

    @Column(name = "contact")
    private String contact;
}
