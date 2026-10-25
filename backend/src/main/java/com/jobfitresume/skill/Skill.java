package com.jobfitresume.skill;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "canonical_name", nullable = false, unique = true)
    private String canonicalName;

    @Column(nullable = false)
    private String category;

    public Skill() {
    }

    public Skill(String name, String canonicalName, String category) {
        this.name = name;
        this.canonicalName = canonicalName;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
