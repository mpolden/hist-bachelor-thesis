package no.kantega.server.model;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

import java.util.List;

public interface TransactionResource {

    @Get
    public Transaction retrieve();
    @Put
    public void store(Transaction transaction);
    @Delete
    public void remove();
}
