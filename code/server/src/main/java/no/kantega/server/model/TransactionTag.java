package no.kantega.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transactionTags")
public class TransactionTag implements Serializable {

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
        return "TransactionTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
