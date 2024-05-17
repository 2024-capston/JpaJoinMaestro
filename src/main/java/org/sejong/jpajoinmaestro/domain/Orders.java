package org.sejong.jpajoinmaestro.domain;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column()
    private LocalDateTime createdAt;

    @Column()
    private String status;

    @OneToMany(mappedBy = "orders")
    private List<OrderDetail> orderDetails;
}