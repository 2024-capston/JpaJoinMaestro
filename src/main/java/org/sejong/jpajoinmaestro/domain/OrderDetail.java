package org.sejong.jpajoinmaestro.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "orders_id", insertable = false, updatable = false)
    private Long orders_id;

    @ManyToOne
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;


    @Column()
    private Long quantity;
}