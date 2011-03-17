package no.kantega.android.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "transactions")
public class Transaction implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(persisted = false)
    private int clientId;
    @DatabaseField
    private Date accountingDate;
    @DatabaseField
    private Date fixedDate;
    @DatabaseField
    private double amountIn;
    @DatabaseField
    private double amountOut;
    @DatabaseField
    private String text;
    @DatabaseField
    private String archiveRef;
    @DatabaseField(foreign = true, columnName = "type_id", foreignAutoRefresh = true)
    private TransactionType type;
    @DatabaseField(foreign = true, columnName = "tag_id", foreignAutoRefresh = true)
    private TransactionTag tag;
    @DatabaseField
    private boolean internal;
    @DatabaseField
    private long timestamp;
    @DatabaseField
    private boolean dirty;
    @DatabaseField
    private boolean changed;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public double getAmountIn() {
        return amountIn;
    }

    public void setAmountIn(double amountIn) {
        this.amountIn = amountIn;
    }

    public double getAmountOut() {
        return amountOut;
    }

    public void setAmountOut(double amountOut) {
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

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        if (Double.compare(that.amountIn, amountIn) != 0) return false;
        if (Double.compare(that.amountOut, amountOut) != 0) return false;
        if (changed != that.changed) return false;
        if (dirty != that.dirty) return false;
        if (id != that.id) return false;
        if (internal != that.internal) return false;
        if (timestamp != that.timestamp) return false;
        if (accountingDate != null ? !accountingDate.equals(that.accountingDate) : that.accountingDate != null)
            return false;
        if (archiveRef != null ? !archiveRef.equals(that.archiveRef) : that.archiveRef != null)
            return false;
        if (fixedDate != null ? !fixedDate.equals(that.fixedDate) : that.fixedDate != null)
            return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (accountingDate != null ? accountingDate.hashCode() : 0);
        result = 31 * result + (fixedDate != null ? fixedDate.hashCode() : 0);
        temp = amountIn != +0.0d ? Double.doubleToLongBits(amountIn) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = amountOut != +0.0d ? Double.doubleToLongBits(amountOut) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (archiveRef != null ? archiveRef.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (internal ? 1 : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (dirty ? 1 : 0);
        result = 31 * result + (changed ? 1 : 0);
        return result;
    }
}
