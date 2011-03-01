package no.kantega.server.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transactionTypes")
public class TransactionType implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransactionType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }
}
