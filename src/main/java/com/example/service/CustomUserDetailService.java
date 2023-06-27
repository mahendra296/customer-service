package com.example.service;

import com.example.entity.Customer;
import com.example.entity.Role;
import com.example.exceptions.ResourceNotFoundException;
import com.example.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Invoke loadUserByUsername method.");
        Customer customer = customerRepository.findByEmail(username);
        if (customer == null) {
            throw new ResourceNotFoundException("User not found with email : " + username);
        }
        Set<Role> roles = customer.getRoles();
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }
        log.info("End loadUserByUsername method.");
        return new User(customer.getEmail(), customer.getPassword(), authorities);
    }
}