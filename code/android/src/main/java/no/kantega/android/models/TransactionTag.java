package no.kantega.android.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transactiontags")
public class TransactionTag {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField(unique = true)
    private String name;

    public TransactionTag() {
    }

    public TransactionTag(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionTag)) return false;
        TransactionTag that = (TransactionTag) o;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
