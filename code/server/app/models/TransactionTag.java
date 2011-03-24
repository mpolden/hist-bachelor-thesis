package models;

import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TransactionTag extends Model {

    @Column(unique = true)
    public String name;
    public int imageId;
}
