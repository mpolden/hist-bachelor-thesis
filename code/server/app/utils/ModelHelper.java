package utils;

import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import models.User;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class ModelHelper {

    private static Logger logger = Logger.getLogger(
            ModelHelper.class.getName());

    public static TransactionTag getOrSaveTag(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        TransactionTag tag = TransactionTag.find("name", name).first();
        if (tag != null) {
            return tag;
        } else {
            tag = new TransactionTag();
            tag.name = name;
            tag.save();
            return tag;
        }
    }

    public static TransactionType getOrAddType(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        TransactionType type = TransactionType.find("name", name).first();
        if (type != null) {
            return type;
        } else {
            type = new TransactionType();
            type.name = name;
            type.save();
            return type;
        }
    }

    public static Transaction saveTransaction(Transaction t, User user) {
        if (t.dirty) {
            Transaction existing = Transaction.findById(t.id);
            if (existing != null) {
                existing._id = t._id;
                existing.accountingDate = t.accountingDate;
                existing.amountIn = t.amountIn;
                existing.amountOut = t.amountOut;
                existing.text = t.text;
                existing.internal = t.internal;
                existing.timestamp = t.timestamp;
                existing.dirty = false;
                existing.tag = ModelHelper.getOrSaveTag(t.tag.name);
                existing.type = ModelHelper.getOrAddType(t.type.name);
                existing.save();
                return existing;
            } else {
                t.id = null;
                t.tag = ModelHelper.getOrSaveTag(t.tag.name);
                t.type = ModelHelper.getOrAddType(t.type.name);
                t.dirty = false;
                t.user = user;
                t.save();
            }
        } else {
            logger.log(Level.WARN,
                    "Not saving non-dirty transaction with _id: " + t._id);
        }
        return t;
    }
}
