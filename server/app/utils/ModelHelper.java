package utils;

import models.Transaction;
import models.TransactionTag;
import models.User;
import play.Logger;

/**
 * This class provides helper methods for updating models
 */
public class ModelHelper {

    /**
     * Save or update the given transaction tag
     *
     * @param t Transaction tag
     * @return The updated transaction tag
     */
    public static TransactionTag saveOrUpdate(TransactionTag t) {
        if (t == null) {
            return null;
        }
        TransactionTag tag = TransactionTag.find("name", t.name).first();
        if (tag != null) {
            return tag;
        } else {
            tag = new TransactionTag();
            tag.name = t.name;
            tag.save();
            return tag;
        }
    }

    /**
     * Save or update the given transaction
     *
     * @param t    Transaction
     * @param user User
     * @return The updated transaction
     */
    public static Transaction saveOrUpdate(Transaction t, User user) {
        if (!t.dirty) {
            Logger.info("Not saving non-dirty transaction with _id: %s", t._id);
            return t;
        }
        if (t.id != null) {
            Transaction existing = Transaction.findById(t.id);
            if (existing != null) {
                existing._id = t._id;
                existing.date = t.date;
                existing.amount = t.amount;
                existing.text = t.text;
                existing.internal = t.internal;
                existing.timestamp = t.timestamp;
                existing.dirty = false;
                existing.tag = ModelHelper.saveOrUpdate(t.tag);
                existing.save();
                return existing;
            }
        }
        t.id = null;
        // Need to update relationship for all foreign fields
        t.tag = ModelHelper.saveOrUpdate(t.tag);
        t.dirty = false;
        t.user = user;
        t.save();
        return t;
    }
}
