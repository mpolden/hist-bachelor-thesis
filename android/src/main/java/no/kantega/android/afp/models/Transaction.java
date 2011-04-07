package no.kantega.android.afp.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * This model class reprents a transaction
 */
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
    private boolean checked;

    /**
     * Empty constructor to satisfy ORMLite
     */
    public Transaction() {
    }

    /**
     * Get the server-side id
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id
     *
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the SQLite generated id
     *
     * @return _id
     */
    public int get_id() {
        return _id;
    }

    /**
     * Set _id
     *
     * @param _id _id
     */
    public void set_id(int _id) {
        this._id = _id;
    }

    /**
     * Get the actual transaction date
     *
     * @return date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set date
     *
     * @param date date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get transaction amount
     *
     * @return amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set amount
     *
     * @param amount amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get the transaction text, this text has already been trimmed
     *
     * @return text
     * @see no.kantega.android.afp.utils.FmtUtil
     */
    public String getText() {
        return text;
    }

    /**
     * Set text
     *
     * @param text text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Get tag for this transaction
     *
     * @return tag
     */
    public TransactionTag getTag() {
        return tag;
    }

    /**
     * Set tag
     *
     * @param tag tag
     */
    public void setTag(TransactionTag tag) {
        this.tag = tag;
    }

    /**
     * Get internal
     *
     * @return internal
     */
    public boolean isInternal() {
        return internal;
    }

    /**
     * Set internal
     *
     * @param internal internal
     */
    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    /**
     * Get the timestamp when the transaction was created
     *
     * @return timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set timestamp
     *
     * @param timestamp timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Check if transaction has been synchronized to the external server
     *
     * @return True if the transaction has not been synchronized
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set dirty
     *
     * @param dirty dirty
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Get checked
     *
     * @return True if Transaction is selected
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Set checked
     *
     * @param checked checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
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
