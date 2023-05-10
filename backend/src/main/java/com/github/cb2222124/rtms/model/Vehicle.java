package com.github.cb2222124.rtms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(unique = true)
    private String registration;

    private String make;

    private String model;

    private int year;

    private int mileage;

    private String colour;

    private boolean sorn;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    @JoinColumn(name = "tax_information_id", referencedColumnName = "id")
    private TaxInformation taxInformation;
}
