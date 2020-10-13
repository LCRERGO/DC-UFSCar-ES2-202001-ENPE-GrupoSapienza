package net.sf.jabref.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Validators {

    public static boolean validateYear(String year) {
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dt = new SimpleDateFormat("yyyy");
            cal.setTime(dt.parse(year));
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean validateBibtexkey(String bibtexkey) {
        if (bibtexkey.length() < 2) {
            return false;
        } else if (!isLetter(bibtexkey.charAt(0))) {
            return false;
        }
        return true;
    }

    private static boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }
}
