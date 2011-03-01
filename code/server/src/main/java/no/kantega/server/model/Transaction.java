package no.kantega.server.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "transactions")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private Date accountingDate;
    private Date fixedDate;
    private Double amountIn;
    private Double amountOut;
    private String text;
    private String archiveRef;

    @ManyToOne(fetch=FetchType.EAGER)
    private TransactionType type;

    @ManyToMany(fetch=FetchType.EAGER)
    private List<TransactionTag> catgories =
            new ArrayList<TransactionTag>();

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public List<TransactionTag> getCatgories() {
        return catgories;
    }

    public void setCatgories(List<TransactionTag> catgories) {
        this.catgories = catgories;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAccountingDate() {
        return accountingDate;
    }

    public void setAccountingDate(Date accountingDate) {
        this.accountingDate = accountingDate;
    }

    public Date getFixedDate() {
        return fixedDate;
    }

    public void setFixedDate(Date fixedDate) {
        this.fixedDate = fixedDate;
    }

    public Double getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(Double amountIn) {
        this.amountIn = amountIn;
    }

    public Double getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(Double amountOut) {
        this.amountOut = amountOut;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getArchiveRef() {
        return archiveRef;
    }

    public void setArchiveRef(String archiveRef) {
        this.archiveRef = archiveRef;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountingDate=" + accountingDate +
                ", fixedDate=" + fixedDate +
                ", amountIn=" + amountIn +
                ", amountOut=" + amountOut +
                ", text='" + text + '\'' +
                ", archiveRef='" + archiveRef + '\'' +
                ", type=" + type +
                ", catgories=" + catgories +
                '}';
    }
}
