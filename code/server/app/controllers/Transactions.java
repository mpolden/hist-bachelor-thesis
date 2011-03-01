package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Transactions extends Controller {

    public static void topTags(int count) {
        //Transaction.find("")
    }

    public static void transactions() {
        List<Transaction> transactionList = Transaction.all().fetch();
        renderJSON(transactionList);
    }

}