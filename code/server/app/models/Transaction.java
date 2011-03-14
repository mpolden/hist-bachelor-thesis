package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Transaction extends Model {

    public Date accountingDate;
    public Date fixedDate;
    public Double amountIn;
    public Double amountOut;
    public String text;
    public String archiveRef;
    public Boolean internal;
    @ManyToOne
    public TransactionType type;
    @ManyToOne
    public TransactionTag tag;
}
