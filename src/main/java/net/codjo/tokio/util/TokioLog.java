/*
 * Team : AGF AM / OSI / SI / BO
 *
 * Copyright (c) 2001 AGF Asset Management.
 */
package net.codjo.tokio.util;
/**
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToPrintStackTrace"})
public final class TokioLog {
    private TokioLog() {
    }


    public static void info(String message) {
        System.out.println(message);
    }


    public static void error(String message) {
        System.err.println(message);
    }


    public static void error(String message, Exception ex) {
        System.err.println(message);
        ex.printStackTrace();
    }
}
