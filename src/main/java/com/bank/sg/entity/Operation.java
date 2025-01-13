package com.bank.sg.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Operation {

    public static final String PREFIX_TABLE = "operation_";

    @Id
    @Column(name = PREFIX_TABLE + "id")
    @GeneratedValue
    private Long id;

    @NotNull
    @Column(name = PREFIX_TABLE + "type")
    private String type;

    @ManyToOne
    @JoinColumn(name = PREFIX_TABLE + "id_sender")
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(name = PREFIX_TABLE + "id_receiver")
    private Account receiverAccount;

    @NotNull
    @Column(name = PREFIX_TABLE + "value")
    private BigDecimal value;

    @NotNull
    @Column(name = PREFIX_TABLE + "date_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime operationDateTime;

}
