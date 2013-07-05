package uni.prakinf.m4;

import java.util.Date;

public class Logger {
    public static void tsl() {
        System.out.print("[" + new Date().toString() + "] ");
    }

    public static void tse() {
        System.err.print("[" + new Date().toString() + "] ");
    }

    public static void errln(String s) {
        tse();
        System.err.println(s);
    }
    public static void errf(String s, String... strings) {
        tse();
        System.err.printf(s, strings);
    }

    public static void logln(String s) {
        tsl();
        System.out.println(s);
    }

    public static void logf(String s) {
        tsl();
        System.out.printf(s);
    }

    public static void logf(String s, String strings) {
        tsl();
        System.out.printf(s, strings);
    }

    public static void logf(String s, String stringa, String stringb) {
        tsl();
        System.out.printf(s, stringa, stringb);
    }

    public static void logf(String s, int val) {
        tsl();
        System.out.printf(s, val);
    }
}
