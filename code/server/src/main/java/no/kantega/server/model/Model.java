package no.kantega.server.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public abstract class Model {

    @Id
    @GeneratedValue
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
