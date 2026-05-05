package com.furniture.api.security;

import com.furniture.api.model.Role;
import com.furniture.api.model.User;
import com.furniture.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom User Details Service
 * Loads user-specific data for Spring Security
 *
 * @author Furniture Team
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Username is actually userId in this implementation
        try {
            Integer userId = Integer.parseInt(username);
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

            return buildUserDetails(user);
        } catch (NumberFormatException e) {
            // If not a number, try to find by email or username
            User user = userRepository.findByEmailOrUsername(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return buildUserDetails(user);
        }
    }

    /**
     * Load user by ID
     */
    @Transactional
    public UserDetails loadUserById(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return buildUserDetails(user);
    }

    /**
     * Build UserDetails from User entity
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
            .username(String.valueOf(user.getUserId()))
            .password(user.getPassword() != null ? user.getPassword() : "")
            .authorities(getAuthorities(user.getRoles()))
            .accountExpired(false)
            .accountLocked(user.isAccountLocked())
            .credentialsExpired(false)
            .disabled(user.isBanned())
            .build();
    }

    /**
     * Convert roles to granted authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
            .collect(Collectors.toList());
    }
}
