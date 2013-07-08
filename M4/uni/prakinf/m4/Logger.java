package uni.prakinf.m4;

import javax.swing.*;
import java.util.Date;

public class Logger {
    public static JTextPane tpane = null;

    private static void addToLog(String s) {
        if (tpane == null)
            System.out.print(s);
        else
            tpane.setText(tpane.getText() + s);
    }

    public static void tsl() {
        addToLog(String.format("[" + new Date().toString() + ",INFO ] "));
    }

    public static void tse() {
        addToLog(String.format("[" + new Date().toString() + ",ERROR] "));
    }

    public static void errln(String s) {
        tse();
        addToLog(s + "\n");
    }

    public static void errf(String s, String stringa) {
        tse();
        addToLog(String.format(s, stringa));
    }

    public static void errf(String s, String stringa, String stringb) {
        tse();
        addToLog(String.format(s, stringa, stringb));
    }

    public static void errf(String s, String stringa, String stringb, String stringc) {
        tse();
        addToLog(String.format(s, stringa, stringb, stringc));
    }

    public static void logln(String s) {
        tsl();
        addToLog(s + "\n");
    }

    public static void logf(String s) {
        tsl();
        addToLog(s);
    }

    public static void logf(String s, String strings) {
        tsl();
        addToLog(String.format(s, strings));
    }

    public static void logf(String s, String stringa, String stringb) {
        tsl();
        addToLog(String.format(s, stringa, stringb));
    }

    public static void logf(String s, int val) {
        tsl();
        addToLog(String.format(s, val));
    }
}
