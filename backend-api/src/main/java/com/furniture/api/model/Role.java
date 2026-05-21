package com.furniture.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Role Entity - Maps to Roles table in MySQL database
 *
 * @author Furniture Team
 */
@Entity
@Table(name = "Roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 20)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<User> users = new HashSet<>();

    // Convenience constructor
    public Role(String roleName) {
        this.roleName = roleName;
    }

    // Role constants
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";
}
