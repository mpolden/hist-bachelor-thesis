package controllers;

import models.Transaction;
import play.Logger;
import play.db.jpa.JPA;
import play.modules.search.Search;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TransactionTags extends Controller {

    @SuppressWarnings("unchecked")
    public static void suggest(String body) {
        List<Map<String, String>> result = Collections.emptyList();
        if (body != null && body.length() > 0) {
            play.modules.search.Query q = Search.search(String.format("text:(%s)", body), Transaction.class);
            List<Long> ids = q.fetchIds();
            Query query = JPA.em().createQuery(
                    "select new map (thetag.name as tag, count(*) as count) from Transaction t" +
                            " join t.tag as thetag" +
                            " where t.id in (:ids)" +
                            " group by thetag.name order by count(*) desc"
            );
            query.setParameter("ids", ids);
            result = query.getResultList();
            if (result.isEmpty()) {
                Logger.warn("Could not find any suggestions for: %s", body);
            }
        }
        renderJSON(result);
    }
}
