package no.kantega.android.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "transactiontypes")
public class TransactionType implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField(unique = true)
    private String name;

    public TransactionType() {
    }

    public TransactionType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionType)) return false;
        TransactionType that = (TransactionType) o;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
