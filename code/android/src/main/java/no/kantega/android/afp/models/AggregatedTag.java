package no.kantega.android.afp.models;

public class AggregatedTag {

    private String name;
    private Double amount;

    public AggregatedTag() {
    }

    public AggregatedTag(Double amount, String name) {
        this.amount = amount;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
