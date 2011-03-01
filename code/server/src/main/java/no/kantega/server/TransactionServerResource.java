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

    @Override
    @Get
    @SuppressWarnings("unchecked")
    public Transaction retrieve() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        List<Transaction> transactionList = session.createCriteria(Transaction.
                class).list();
        session.close();
        return transactionList.size() > 0 ? transactionList.get(0) : null;
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
