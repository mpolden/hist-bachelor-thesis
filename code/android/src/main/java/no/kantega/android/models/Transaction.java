package no.kantega.android.models;

import java.util.Date;

public class Transaction {

    private Date accountingDate;
    private Date fixedDate;
    private Double amountIn;
    private Double amountOut;
    private String text;
    private String archiveRef;
    private TransactionType type;
    private TransactionTag tag;
    private Boolean internal;
    private Long timestamp;

    public Transaction() {
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

    public TransactionTag getTag() {
        return tag;
    }

    public void setTag(TransactionTag tag) {
        this.tag = tag;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        if (accountingDate != null ? !accountingDate.equals(that.accountingDate) : that.accountingDate != null)
            return false;
        if (amountIn != null ? !amountIn.equals(that.amountIn) : that.amountIn != null)
            return false;
        if (amountOut != null ? !amountOut.equals(that.amountOut) : that.amountOut != null)
            return false;
        if (archiveRef != null ? !archiveRef.equals(that.archiveRef) : that.archiveRef != null)
            return false;
        if (fixedDate != null ? !fixedDate.equals(that.fixedDate) : that.fixedDate != null)
            return false;
        if (internal != null ? !internal.equals(that.internal) : that.internal != null)
            return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = accountingDate != null ? accountingDate.hashCode() : 0;
        result = 31 * result + (fixedDate != null ? fixedDate.hashCode() : 0);
        result = 31 * result + (amountIn != null ? amountIn.hashCode() : 0);
        result = 31 * result + (amountOut != null ? amountOut.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (archiveRef != null ? archiveRef.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (internal != null ? internal.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
