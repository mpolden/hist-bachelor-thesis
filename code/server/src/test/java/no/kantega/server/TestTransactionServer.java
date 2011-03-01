package no.kantega.server;

import no.kantega.server.model.Transaction;
import no.kantega.server.model.TransactionResource;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

import java.util.Date;

import static org.junit.Assert.*;

public class TestTransactionServer {

    @BeforeClass
    public static void setUp() throws Exception {
        new Server(Protocol.HTTP, 8182, TransactionServerResource.class).
                start();
    }

    @Test
    public void testStore() throws Exception {
        ClientResource clientResource =
                new ClientResource("http://127.0.0.1:8182");

        TransactionResource transactionResource = clientResource.wrap(
                TransactionResource.class);

        Transaction transaction = new Transaction();

        transactionResource.store(transaction);
        Transaction otherTransaction = transactionResource.retrieve();
    }
}
