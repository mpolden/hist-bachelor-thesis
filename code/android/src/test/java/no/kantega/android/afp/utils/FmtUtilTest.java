package no.kantega.android.afp.utils;

import org.junit.Test;

import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

public class FmtUtilTest {

    @Test
    public void testDateToString() {
        String expected = "2010-01-01 00:00:00";
        String actual = FmtUtil.dateToString("yyyy-MM-dd HH:mm:ss",
                new Date(1262300400000L));
        assertEquals(expected, actual);
    }

    @Test
    public void testStringToDate() {
        Date expected = new Date(1262300400000L);
        Date actual = FmtUtil.stringToDate("yyyy-MM-dd HH:mm:ss",
                "2010-01-01 00:00:00");
        assertEquals(expected, actual);
    }

    @Test
    public void testCurrency() {
        String expected = "$1,199.49";
        String actual = FmtUtil.currency(1199.492349, Locale.US);
        assertEquals(expected, actual);
    }

    @Test
    public void testTrimTransactionText() {
        String expected;
        String actual;
        expected = "STATOIL INNHERREDSVEIE";
        actual = FmtUtil.trimTransactionText(
                "456997107150**** 29.03 NOK 500,37 STATOIL INNHERREDSVEIE");
        assertEquals(expected, actual);
        expected = "JACK & JONES OSTERSUND";
        actual = FmtUtil.trimTransactionText(
                "456997107150**** 08.04 SEK 1180,00 JACK & JONES OSTERSUND");
        assertEquals(expected, actual);
        expected = "JACK & JONES OSTERSUND";
        actual = FmtUtil.trimTransactionText(
                "4569971071500000 08.04 SEK 1180,00 JACK & JONES OSTERSUND");
        assertEquals(expected, actual);
        expected = "NIDAR AS BLOMSTADVN 2 TRONDHEIM";
        actual = FmtUtil.trimTransactionText(
                "27.03 NIDAR AS BLOMSTADVN 2 TRONDHEIM");
        assertEquals(expected, actual);
        expected = "SATS Norge AS                      BETNR:       279";
        actual = FmtUtil.trimTransactionText(
                "TIL: SATS Norge AS                      BETNR:       279");
        assertEquals(expected, actual);
        expected = "";
        actual = FmtUtil.trimTransactionText(null);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNumber() {
        assertFalse(FmtUtil.isNumber(null));
        assertFalse(FmtUtil.isNumber(""));
        assertFalse(FmtUtil.isNumber("foo"));
        assertFalse(FmtUtil.isNumber(("123.")));
        assertFalse(FmtUtil.isNumber(("123,")));
        assertTrue(FmtUtil.isNumber("123"));
        assertTrue(FmtUtil.isNumber("123,45"));
        assertTrue(FmtUtil.isNumber("123.45"));
    }
}
