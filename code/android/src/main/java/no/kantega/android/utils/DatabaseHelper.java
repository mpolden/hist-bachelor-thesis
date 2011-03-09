package no.kantega.android.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import no.kantega.android.models.Transaction;
import no.kantega.android.models.TransactionTag;

public class DatabaseHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void insert(final SQLiteDatabase db, final Transaction t) {
        // Transaction type
        ContentValues values = new ContentValues();
        values.put("name", t.getType().getName());
        final long typeId = db.insertWithOnConflict("transactiontype", null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.d(TAG, "Inserted transaction type with ID: " + typeId);
        // Transaction
        values = new ContentValues();
        values.put("accountingdate", FmtUtil.date(SQLITE_DATE_FORMAT,
                t.getAccountingDate()));
        values.put("amountin", t.getAmountIn());
        values.put("amountout", t.getAmountOut());
        values.put("archiveref", t.getArchiveRef());
        values.put("fixeddate", FmtUtil.date(SQLITE_DATE_FORMAT, t.getFixedDate()));
        values.put("text", t.getText());
        values.put("type_id", typeId);
        final long transactionId = db.insert("\"transaction\"", null, values);
        Log.d(TAG, "Inserted transaction with ID: " + transactionId);
        // Transaction tag
        for (TransactionTag tag : t.getTags()) {
            values = new ContentValues();
            values.put("name", tag.getName());
            long transactionTagId = db.insertWithOnConflict("transactiontag",
                    null, values, SQLiteDatabase.CONFLICT_IGNORE);
            // Add many to many relation
            values = new ContentValues();
            values.put("transaction_id", transactionId);
            values.put("tags_id", transactionTagId);
            db.insertWithOnConflict("transaction_transactiontag", null, values,
                    SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public static void getOrderedByDateDesc(SQLiteDatabase db, int limit) {
        final Cursor cursor = db.query(
                "\"transaction\" " +
                        "INNER JOIN \"transactiontype\" " +
                        "ON transactiontype.id = \"transaction.type_id\""
                , null, null, null,
                null, null, "accountingdate DESC", String.valueOf(limit));
        //cursor.
    }
}
