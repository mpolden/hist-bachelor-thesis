package no.kantega.android.afp.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = "transactions")
public class Transaction implements Serializable {

    @DatabaseField
    private int id;
    @DatabaseField(generatedId = true)
    private int _id;
    @DatabaseField
    private Date date;
    @DatabaseField
    private double amount;
    @DatabaseField
    private String text;
    @DatabaseField(foreign = true, columnName = "tag_id", foreignAutoRefresh = true, canBeNull = true)
    private TransactionTag tag;
    @DatabaseField(index = true)
    private boolean internal;
    @DatabaseField(index = true)
    private long timestamp;
    @DatabaseField(index = true)
    private boolean dirty;

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;

        Transaction that = (Transaction) o;

        if (_id != that._id) return false;
        if (Double.compare(that.amount, amount) != 0) return false;
        if (dirty != that.dirty) return false;
        if (id != that.id) return false;
        if (internal != that.internal) return false;
        if (timestamp != that.timestamp) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + _id;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        temp = amount != +0.0d ? Double.doubleToLongBits(amount) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (internal ? 1 : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (dirty ? 1 : 0);
        return result;
    }
}
