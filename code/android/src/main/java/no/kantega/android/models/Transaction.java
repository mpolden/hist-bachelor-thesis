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
        if (tags != null ? !tags.equals(that.tags) : that.tags != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null)
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
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}
