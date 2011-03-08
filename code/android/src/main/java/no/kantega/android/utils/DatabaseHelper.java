package no.kantega.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "transaction.db";
    private static final int DATABASE_VERSION = 1;
    
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
                    "type_id INTEGER," +
                    "FOREIGN KEY(type_id) REFERENCES transactiontype(id)" +
                    ");";

    private static final String TRANSACTION_TRANSACTIONTAG_TABLE_CREATE =
            "CREATE TABLE \"transaction_transactiontag\" (" +
                    "    transaction_id INTEGER NOT NULL," +
                    "    tags_id INTEGER NOT NULL," +
                    "    FOREIGN KEY(transaction_id) REFERENCES \"transaction\" (id)," +
                    "    FOREIGN KEY(tags_id) REFERENCES transactiontag(id)" +
                    ");";

    private static final String INDEXES_CREATE =
            "CREATE UNIQUE INDEX transactiontype_name_key ON transactiontype (name);" +
            "CREATE UNIQUE INDEX transactiontag_name_key ON transactiontag (name);";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database");
        db.execSQL(TRANSACTIONTYPE_TABLE_CREATE);
        db.execSQL(TRANSACTIONTAG_TABLE_CREATE);
        db.execSQL(TRANSACTION_TABLE_CREATE);
        db.execSQL(TRANSACTION_TRANSACTIONTAG_TABLE_CREATE);
        db.execSQL(INDEXES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.d(TAG, "Upgrading database");
        // XXX: Implement proper upgrading
        db.execSQL("DROP TABLE IF EXISTS \"transaction_transactiontag\"");
        db.execSQL("DROP TABLE IF EXISTS \"transaction\"");
        db.execSQL("DROP TABLE IF EXISTS \"transactiontag\"");
        db.execSQL("DROP TABLE IF EXISTS \"transactiontype\"");
        db.execSQL("DROP INDEX IF EXISTS \"transactiontag_name_key\"");
        db.execSQL("DROP INDEX IF EXISTS \"transactiontype_name_key\"");
        onCreate(db);
    }
}
