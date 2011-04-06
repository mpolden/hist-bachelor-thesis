package no.kantega.android.afp.controllers;

import no.kantega.android.afp.MavenizedTestRunner;
import no.kantega.android.afp.OverviewActivity;
import no.kantega.android.afp.utils.DatabaseHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MavenizedTestRunner.class)
public class TransactionsTest {

    private Transactions transactions;

    @Before
    public void setUp() {
        OverviewActivity activity = new OverviewActivity();
        this.transactions = new Transactions(activity.getApplicationContext(),
                new DatabaseHelper(activity.getApplicationContext(), null, 1));
    }

    @Test
    public void testAdd() {
        // XXX: Not working until Robolectra implements android.database.sqlite.SQLiteDatabase.rawQuery
        /*final Transaction t = new Transaction();
        t.setDate(new Date());
        t.setAmount(133.7);
        t.setText("Test");
        t.setId(1);
        final TransactionTag tag = new TransactionTag();
        tag.setId(1);
        tag.setName("Test");
        t.setTag(tag);
        t.setTimestamp(t.getDate().getTime());
        t.setInternal(false);
        t.setDirty(true);
        transactions.add(t);
        assertTrue(transactions.getCount() == 1);*/
    }

}
