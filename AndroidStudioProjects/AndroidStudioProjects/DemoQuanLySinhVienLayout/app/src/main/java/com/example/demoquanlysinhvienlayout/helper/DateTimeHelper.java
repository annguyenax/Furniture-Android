package com.example.demoquanlysinhvienlayout.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static Date toDate(String s) throws ParseException {
        return sdf.parse(s);
    }

    public static String toString(Date date) {
        return sdf.format(date);
    }
}