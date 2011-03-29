package utils;

import models.Transaction;
import models.TransactionTag;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ModelHelper {

    private static Logger logger = Logger.getLogger(
            ModelHelper.class.getName());

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
            tag.imageId = t.imageId;
            tag.save();
            return tag;
        }
    }

    public static Transaction saveOrUpdate(Transaction t, User user) {
        if (!t.dirty) {
            logger.log(Level.INFO,
                    "Not saving non-dirty transaction with _id: " + t._id);
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
