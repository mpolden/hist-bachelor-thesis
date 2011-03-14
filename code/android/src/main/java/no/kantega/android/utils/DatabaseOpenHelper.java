package no.kantega.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "transaction.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TRANSACTIONTYPE_TABLE_CREATE =
            "CREATE TABLE \"transactiontype\" (" +
                    "    id INTEGER PRIMARY KEY," +
                    "    name TEXT" +
                    ");";
    private static final String TRANSACTIONTAG_TABLE_CREATE =
            "CREATE TABLE \"transactiontag\" (" +
                    "    id INTEGER PRIMARY KEY," +
                    "    name TEXT" +
                    ");";
    private static final String TRANSACTION_TABLE_CREATE =
            "CREATE TABLE \"transaction\" (" +
                    "id INTEGER PRIMARY KEY," +
                    "accountingdate TEXT," +
                    "amountin REAL," +
                    "amountout REAL," +
                    "archiveref TEXT," +
                    "fixeddate TEXT," +
                    "text TEXT," +
                    "timestamp INTEGER," +
                    "internal INTEGER," +
                    "type_id INTEGER," +
                    "tag_id INTEGER," +
                    "FOREIGN KEY(type_id) REFERENCES transactiontype(id)" +
                    "FOREIGN KEY(tag_id) REFERENCES transactiontag(id)" +
                    ");";
    private static final String TRANSACTIONTYPE_INDEX_CREATE =
            "CREATE UNIQUE INDEX transactiontype_name_key ON transactiontype (name);";
    private static final String TRANSACTIONTAG_INDEX_CREATE =
            "CREATE UNIQUE INDEX transactiontag_name_key ON transactiontag (name);";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when creating the initial database
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database");
        db.execSQL(TRANSACTIONTYPE_TABLE_CREATE);
        db.execSQL(TRANSACTIONTAG_TABLE_CREATE);
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(TRANSACTIONTYPE_INDEX_CREATE);
        db.execSQL(TRANSACTIONTAG_INDEX_CREATE);
    }

    /**
     * Called when upgrading an existing database
     *
     * @param db
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "Upgrading database");
        // XXX: Implement proper upgrading
        dropTables(db);
        onCreate(db);
    }

    /**
     * Drop all tables
     *
     * @param db
     */
    public void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS \"transaction\"");
        db.execSQL("DROP TABLE IF EXISTS \"transactiontag\"");
        db.execSQL("DROP TABLE IF EXISTS \"transactiontype\"");
        db.execSQL("DROP INDEX IF EXISTS \"transactiontag_name_key\"");
        db.execSQL("DROP INDEX IF EXISTS \"transactiontype_name_key\"");
    }
}
