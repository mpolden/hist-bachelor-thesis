package controllers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.db.jpa.JPA;
import play.mvc.Controller;

import javax.persistence.Query;
import java.util.List;

public class TransactionTags extends Controller {

    private static final Logger logger = Logger.getLogger(
            TransactionTags.class.getName());

    private static String firstWord(String s) {
        if (s != null) {
            final String[] words = s.split(" ");
            if (words.length > 0) {
                return words[0];
            }
        }
        return null;
    }

    private static boolean renderTag(List<Object> result) {
        if (!result.isEmpty()) {
            renderText(result.get(0));
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static void suggest(String body) {
        if (body != null) {
            Query query = JPA.em().createQuery(
                    "select tag.name from Transaction t" +
                            " join t.tag as tag" +
                            " where t.trimmedText = :text" +
                            " group by tag.name order by count(*)"
            );
            query.setParameter("text", body);
            if (!renderTag(query.getResultList())) {
                // No exact match found, go nuts
                final String firstWord = firstWord(body);
                query = JPA.em().createQuery(
                        "select tag.name from Transaction t" +
                                " join t.tag as tag" +
                                " where lower(t.trimmedText) like :text" +
                                " group by tag.name order by count(*)"
                );
                query.setParameter("text", String.format("%%%s%%",
                        firstWord.toLowerCase()));
                if (!renderTag(query.getResultList())) {
                    logger.log(Level.WARN, "Could not find tag for text: " +
                            body);
                }
            }
        }
    }
}
