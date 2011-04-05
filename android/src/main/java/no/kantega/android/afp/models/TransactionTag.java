package no.kantega.android.afp.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * This model class reperesents a transaction tag
 */
@DatabaseTable(tableName = "transactiontags")
public class TransactionTag implements Serializable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique = true, canBeNull = false)
    private String name;

    /**
     * Empty constructor to satisfy ORMLite
     */
    public TransactionTag() {
    }

    /**
     * Create a transaction tag with the given name
     *
     * @param name Name of tag
     */
    public TransactionTag(String name) {
        this.name = name;
    }

    /**
     * Get id
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
     * Get name
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionTag)) return false;
        TransactionTag that = (TransactionTag) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
