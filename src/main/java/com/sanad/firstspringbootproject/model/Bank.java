package com.sanad.firstspringbootproject.model;


import jakarta.persistence.*;

@Entity
@Table(
        name = "banks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_banks_normalized_name",
                        columnNames = "normalized_name"
                )
        }
)
public class Bank {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "normalized_name",  nullable = false, unique = true)
    private String normalizedName;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected Bank() {}

    public Bank(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public Long getVersion() {
        return version;
    }

    public void rename(String name, String normalizedName) {
        this.name = name;
        this.normalizedName = normalizedName;
    }
}
