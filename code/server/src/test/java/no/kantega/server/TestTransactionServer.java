package no.kantega.server;

import no.kantega.server.TransactionServerResource;
import no.kantega.server.model.Transaction;
import no.kantega.server.model.TransactionResource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;

import java.util.Date;
import java.util.List;

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
        transaction.setActor("Big Bite");
        transaction.setAmount(45);
        transaction.setDate(new Date());

        /*transactionResource.store(transaction);
        List<Transaction> transactionList = clientResource.get(List.class);
        assertNotNull(transactionList);
        assertEquals(transactionList.size(), 1);
        Transaction otherTransaction = transactionList.get(0);
        assertEquals(transaction.getActor(), otherTransaction.getActor());
        assertEquals(transaction.getDate(), otherTransaction.getDate());
        assertEquals(transaction.getAmount(), otherTransaction.getAmount());*/
    }
}
