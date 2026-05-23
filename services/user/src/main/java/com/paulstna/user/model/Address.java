package com.paulstna.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserProfile userProfile;

    private String alias;
    private String street;
    private String city;
    private String state;
    private String postalCode;

    @Column(length = 2)
    private String countryCode;

    @Column(nullable = false)
    @Builder.Default
    private boolean isDefault = false;

    @Column(columnDefinition = "TEXT")
    private String instructions;
}