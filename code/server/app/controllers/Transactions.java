package controllers;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;

import java.util.*;

import models.*;

import javax.persistence.Query;

public class Transactions extends Controller {

    public static void topTags(int count) {
        //List<TransactionTag> tags = TransactionTag.find("")
        Query query = JPA.em().createQuery(
                "select tag.name, sum(t.amountOut) from Transaction t join t.tags as tag" +
                        " group by tag.name");
        List l = query.getResultList();
        for (Object o : l) {
            renderText(o);
        }
        //renderJSON(tags);
    }

    public static void transactions() {
        List<Transaction> transactionList = Transaction.all().fetch();
        renderJSON(transactionList);
    }

}