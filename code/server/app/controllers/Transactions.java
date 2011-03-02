package controllers;

import models.Transaction;
import play.db.jpa.JPA;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.List;

public class Transactions extends Controller {

    @SuppressWarnings("unchecked")
    public static void topTags(int count) {
        Query query = JPA.em().createQuery(
                "select tag.name, sum(t.amountOut) from Transaction t join t.tags as tag" +
                        " group by tag.name order by sum(t.amountOut) desc").
                setMaxResults(count);
        List<Object[]> result = query.getResultList();
        StringBuilder out = new StringBuilder();
        for (Object[] o : result) {
            out.append(o[0]);
            out.append(" ");
            out.append(o[1]);
            out.append("\n");
        }
        renderText(out.toString());
    }

    public static void transactions() {
        List<Transaction> transactionList = Transaction.all().fetch();
        renderJSON(transactionList);
    }
}