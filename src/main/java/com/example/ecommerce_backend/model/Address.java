package com.example.ecommerce_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

    @Column(name = "country", nullable = false, length = 75)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address_line_two", length = 512)
    private String addressLineTwo;

    @Column(name = "address_line_one", nullable = false, length = 512)
    private String addressLineOne;

}