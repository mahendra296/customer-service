package com.example.entity;


import com.example.entity.auditable.Auditable;
import com.example.enums.ShopStatus;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "shop")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shop extends Auditable {

    private String name;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private ShopStatus status = ShopStatus.OPEN;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private Set<Menu> menus = new HashSet<Menu>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "shop")
    private Set<Order> orders = new HashSet<Order>();
}
