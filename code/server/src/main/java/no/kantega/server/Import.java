package no.kantega.server;

import no.kantega.server.model.TransactionTag;
import no.kantega.server.model.Transaction;
import no.kantega.server.model.TransactionType;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Import {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.printf("usage: java %s <filepath>",
                    Import.class.getName());
            System.exit(1);
        }
        File f = new File(args[0]);
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                addTransaction(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        printObjects();

    }

    @SuppressWarnings("unchecked")
    private static void printObjects() {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        List<Transaction> transactionList = session.createCriteria(Transaction.
                class).list();
        session.close();
        for (Transaction t : transactionList) {
            System.out.println(t);
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd.MM.yyyy");

    private static void addTransaction(String line) {
        String[] s = line.split("\\|");
        try {
            Date accountingDate = dateFormat.parse(s[0]);
            Date fixedDate = dateFormat.parse(s[1]);
            String archiveRef = s[2];
            TransactionType type = addType(s[3]);
            String text = s[4];
            Double out = Double.parseDouble(s[5]);
            Double in = Double.parseDouble(s[6]);
            TransactionTag transactionTag = addCategory(s[8]);

            Transaction t = new Transaction();
            t.setAccountingDate(accountingDate);
            t.setFixedDate(fixedDate);
            t.setArchiveRef(archiveRef);
            t.setType(type);
            t.setText(text);
            t.setAmountOut(out);
            t.setAmountIn(in);
            t.getCatgories().add(transactionTag);


            addTransaction(t);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void addTransaction(Transaction t) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();
        session.saveOrUpdate(t);
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    private static TransactionTag addCategory(String name) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();

        List<TransactionTag> tags = session.createCriteria(
                TransactionTag.class).add(Restrictions.eq("name", name)).
                list();
        if (tags.size() > 0) {
            session.close();
            return tags.get(0);
        } else {
            TransactionTag c = new TransactionTag();
            c.setName(name);
            session.saveOrUpdate(c);
            session.getTransaction().commit();
            return c;
        }
    }

    @SuppressWarnings("unchecked")
    private static TransactionType addType(String name) {
        Session session = HibernateUtil.getSession();
        session.beginTransaction();

        List<TransactionType> types = session.createCriteria(
                TransactionType.class).add(Restrictions.eq("type", name)).
                list();
        if (types.size() > 0) {
            session.close();
            return types.get(0);
        } else {
            TransactionType transactionType = new TransactionType();
            transactionType.setType(name);
            session.saveOrUpdate(transactionType);
            session.getTransaction().commit();
            return transactionType;
        }

    }

}
