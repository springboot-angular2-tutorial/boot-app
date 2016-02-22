package com.myapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dummy")
public class Dual {
    @Id
    @Column(name = "dummy")
    private String dummy;
}
