import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import models.User;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import play.vfs.VirtualFile;
import utils.FmtUtil;
import utils.ModelHelper;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@OnApplicationStart
public class Import extends Job {

    private static final Logger logger = Logger.getLogger(
            Import.class.getName());
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd.MM.yyyy");

    public void doJob() {
        loadUsersFromFixture();
        loadTransactionsFromCsv();
    }

    private void loadUsersFromFixture() {
        if (User.count() == 0) {
            Fixtures.load("fixtures-local.yml");
        }
    }

    private void loadTransactionsFromCsv() {
        if (Transaction.count() == 0) {
            File f = VirtualFile.fromRelativePath(
                    "/conf/fixture-transactions-full.csv").getRealFile();
            User user = User.all().first();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                String line;
                while ((line = reader.readLine()) != null) {
                    Transaction t = parseTransaction(line);
                    if (t != null) {
                        t.user = user;
                        t.save();
                    }
                }
            } catch (IOException e) {
                logger.log(Level.ERROR, e);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }

    private Transaction parseTransaction(String line) {
        String[] s = line.split("_");
        Date accountingDate = parseDate(s[0]);
        String text = s[4];
        Double out = Double.parseDouble(s[5]);
        Double in = Double.parseDouble(s[6]);
        Transaction t = new Transaction();
        t.accountingDate = accountingDate;
        TransactionType type = new TransactionType();
        type.name = s[3];
        t.type = ModelHelper.insertIgnoreType(type);
        t.text = text;
        t.trimmedText = FmtUtil.trimTransactionText(text).trim();
        t.amountOut = out;
        t.amountIn = in;
        if (!"null".equals(s[7])) {
            TransactionTag tag = new TransactionTag();
            tag.name = s[7];
            t.tag = ModelHelper.insertIgnoreTag(tag);
            t.tag.imageId = Integer.parseInt(s[8], 16);
            t.tag.save();
        } else {
            t.tag = null;
        }
        t.internal = false;
        t.dirty = false;
        t.timestamp = t.accountingDate.getTime();
        return t;
    }

    private Date parseDate(String s) {
        try {
            return dateFormat.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}
