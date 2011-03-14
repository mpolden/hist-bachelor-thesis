package no.kantega.android.models;

public class TransactionTag {

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
}
