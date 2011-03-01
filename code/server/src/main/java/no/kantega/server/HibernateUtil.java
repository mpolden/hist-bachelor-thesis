package no.kantega.server;

import no.kantega.server.model.Transaction;
import no.kantega.server.model.TransactionCategory;
import no.kantega.server.model.TransactionType;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.metadata.ClassMetadata;

import java.util.Map;

public class HibernateUtil {

    private static final SessionFactory ourSessionFactory;

    static {
        try {
            ourSessionFactory = new AnnotationConfiguration().
                    addPackage("no.kantega.server.model").
                    addAnnotatedClass(Transaction.class).
                    addAnnotatedClass(TransactionType.class).
                    addAnnotatedClass(TransactionCategory.class).
                    configure("hibernate.cfg.xml").
                    buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }
}
