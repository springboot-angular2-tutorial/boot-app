package com.myapp.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "micropost")
@Data
@NoArgsConstructor
public class Micropost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @NotNull
    private String content;

    @NotNull
    @Column(name = "created_at")
    private Date createdAt;

    public Micropost(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public Micropost(String content) {
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
    }

}
