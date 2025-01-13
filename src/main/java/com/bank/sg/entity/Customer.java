package com.bank.sg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {

    public static final String PREFIX_TABLE = "customer_";

    @Id
    @Column(name = PREFIX_TABLE + "id")
    private Long id;

    @NotNull
    @NotBlank(message = "Name is mandatory")
    @Column(name = PREFIX_TABLE + "name")
    private String name;
}
