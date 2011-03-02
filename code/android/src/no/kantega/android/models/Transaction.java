package no.kantega.android.models;

import java.util.Date;
import java.util.List;

public class Transaction {

    public Date accountingDate;
    public Date fixedDate;
    public Double amountIn;
    public Double amountOut;
    public String text;
    public String archiveRef;
    public TransactionType type;
    public List<TransactionTag> tags;
}
