package org.sejong.jpajoinmaestro.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orders_id", nullable = false)
    private Orders orders;

    @CreationTimestamp
    @Column()
    private LocalDateTime createdAt;

    @Column()
    private String shipmentStatus;
}

