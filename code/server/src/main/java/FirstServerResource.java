import no.kantega.server.model.Transaction;
import org.hibernate.Session;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.List;

public class FirstServerResource extends ServerResource {

    public static void main(String[] args) throws Exception {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        Transaction t = new Transaction();
        t.setActor("Big bite");
        t.setAmount(45);
        session.save(t);
        session.getTransaction().commit();
        new Server(Protocol.HTTP, 8182, FirstServerResource.class).start();
    }

    @Get
    public List<Transaction> retrieve() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        List<Transaction> transactionList = session.createCriteria(Transaction.
                class).list();
        session.close();
        return transactionList;
    }
}
