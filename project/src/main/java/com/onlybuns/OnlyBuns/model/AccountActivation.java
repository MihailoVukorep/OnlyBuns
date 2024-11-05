package com.onlybuns.OnlyBuns.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_activations")
public class AccountActivation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne
    protected Account account;

    @Column
    protected AccountActivationStatus status;

    @Column(nullable = false, unique = true)
    private String token;

    @Override
    public String toString() {
        return "AccountActivation{" +
                "id=" + id +
                ", createdDate=" + createdDate +
                ", account=" + account.getId() +
                ", status=" + status +
                ", token='" + token + '\'' +
                '}';
    }
}
