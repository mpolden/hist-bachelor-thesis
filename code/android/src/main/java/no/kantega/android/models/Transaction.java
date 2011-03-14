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
}
