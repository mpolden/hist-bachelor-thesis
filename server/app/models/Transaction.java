package models;

import play.db.jpa.Model;
import play.modules.search.Field;
import play.modules.search.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * This model class represents a transaction
 */
@Entity
@Indexed
public class Transaction extends Model {

    public int _id; // ID generated on device
    public Date date;
    public double amount;
    @Field
    public String text;
    public boolean internal;
    public long timestamp;
    public boolean dirty;
    @ManyToOne
    public TransactionTag tag;
    @ManyToOne
    public User user;
}
