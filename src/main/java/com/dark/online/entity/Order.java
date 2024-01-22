package com.dark.online.entity;

import com.dark.online.enums.OrderTypeEnum;
import com.dark.online.enums.PaymentTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User sellerId;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyerId;

    private String name;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private String description;
    private OrderTypeEnum orderType;
}
