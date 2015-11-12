package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "relationship", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
@NoArgsConstructor
@Data
public class Relationship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private User follower;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private User followed;

    public Relationship(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }
}
