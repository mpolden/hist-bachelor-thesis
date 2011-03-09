package no.kantega.android.models;

import java.util.Date;
import java.util.List;

public class Transaction {

    private Date accountingDate;
    private Date fixedDate;
    private Double amountIn;
    private Double amountOut;
    private String text;
    private String archiveRef;
    private TransactionType type;
    private List<TransactionTag> tags;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        if (getAccountingDate() != null ? !getAccountingDate().equals(that.getAccountingDate()) : that.getAccountingDate() != null)
            return false;
        if (getAmountIn() != null ? !getAmountIn().equals(that.getAmountIn()) : that.getAmountIn() != null)
            return false;
        if (getAmountOut() != null ? !getAmountOut().equals(that.getAmountOut()) : that.getAmountOut() != null)
            return false;
        if (getArchiveRef() != null ? !getArchiveRef().equals(that.getArchiveRef()) : that.getArchiveRef() != null)
            return false;
        if (getFixedDate() != null ? !getFixedDate().equals(that.getFixedDate()) : that.getFixedDate() != null)
            return false;
        if (getTags() != null ? !getTags().equals(that.getTags()) : that.getTags() != null)
            return false;
        if (getText() != null ? !getText().equals(that.getText()) : that.getText() != null)
            return false;
        if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getAccountingDate() != null ? getAccountingDate().hashCode() : 0;
        result = 31 * result + (getFixedDate() != null ? getFixedDate().hashCode() : 0);
        result = 31 * result + (getAmountIn() != null ? getAmountIn().hashCode() : 0);
        result = 31 * result + (getAmountOut() != null ? getAmountOut().hashCode() : 0);
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        result = 31 * result + (getArchiveRef() != null ? getArchiveRef().hashCode() : 0);
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (getTags() != null ? getTags().hashCode() : 0);
        return result;
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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public List<TransactionTag> getTags() {
        return tags;
    }

    public void setTags(List<TransactionTag> tags) {
        this.tags = tags;
    }
}
