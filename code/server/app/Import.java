import models.Transaction;
import models.TransactionTag;
import models.TransactionType;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Import extends Job {

    public void doJob() {
        System.out.println("hello?");
        File f = new File("/home/martin/Downloads/transactions.csv");
        try {
            FileInputStream fis = new FileInputStream(f);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis));
            String line;
            while ((line = reader.readLine()) != null) {
                addTransaction(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        printObjects();
    }

    private void printObjects() {
        List<Transaction> transactionList = Transaction.all().fetch();
        for (Transaction t : transactionList) {
            System.out.println(t);
        }
    }

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd.MM.yyyy");

    private void addTransaction(String line) {
        String[] s = line.split("\\|");
        try {
            Date accountingDate = dateFormat.parse(s[0]);
            Date fixedDate = dateFormat.parse(s[1]);
            String archiveRef = s[2];
            TransactionType type = addType(s[3]);
            String text = s[4];
            Double out = Double.parseDouble(s[5]);
            Double in = Double.parseDouble(s[6]);
            TransactionTag transactionTag = addCategory(s[8]);
            Transaction t = new Transaction();
            t.accountingDate = accountingDate;
            t.fixedDate = fixedDate;
            t.archiveRef = archiveRef;
            t.type = type;
            t.text = text;
            t.amountOut = out;
            t.amountIn = in;
            t.tags.add(transactionTag);
            t.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private TransactionTag addCategory(String name) {
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

    private TransactionType addType(String name) {
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
}
