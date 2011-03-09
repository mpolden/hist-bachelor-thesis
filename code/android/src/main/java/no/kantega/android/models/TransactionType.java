package no.kantega.android.models;

public class TransactionType {

    private String name;

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
}
