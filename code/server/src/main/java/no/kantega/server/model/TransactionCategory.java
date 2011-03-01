package no.kantega.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transactionCategories")
public class TransactionCategory implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TransactionCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
