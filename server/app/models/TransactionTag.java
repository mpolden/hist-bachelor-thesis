package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * This model class represents a transaction tag
 */
@Entity
public class TransactionTag extends Model {

    @Column(unique = true)
    public String name;

    /**
     * Empty constructor to satisfy Hibernate
     */
    public TransactionTag() {
    }

    /**
     * Create a new transaction tag with the given name
     *
     * @param name Name
     */
    public TransactionTag(String name) {
        this.name = name;
    }
}
