package no.kantega.server;

import no.kantega.server.model.Transaction;
import no.kantega.server.model.TransactionResource;
import org.hibernate.Session;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import java.util.List;

public class TransactionServerResource extends ServerResource
        implements TransactionResource {

    public static void main(String[] args) throws Exception {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        Transaction t = new Transaction();
        t.setActor("Big bite");
        t.setAmount(45);
        session.save(t);
        session.getTransaction().commit();
        new Server(Protocol.HTTP, 8182, TransactionServerResource.class).
                start();
    }

    @Override
    @Get
    public List<Transaction> retrieve() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        List<Transaction> transactionList = session.createCriteria(Transaction.
                class).list();
        session.close();
        return transactionList;
    }

    @Override
    @Put
    public void store(Transaction transaction) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.saveOrUpdate(transaction);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    @Delete
    public void remove() {
    }
}
