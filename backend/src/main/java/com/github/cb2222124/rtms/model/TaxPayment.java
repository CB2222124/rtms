package com.github.cb2222124.rtms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaxPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private Owner owner;

    @ManyToOne
    private Vehicle vehicle;

    @ManyToOne
    private TaxClass taxClass;

    private LocalDate datePaid;

    private Long pricePencePaid;
}
