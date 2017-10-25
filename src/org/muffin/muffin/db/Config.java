package org.muffin.muffin.db;

public class Config {
    private static String URL = "jdbc:postgresql://localhost:5080/postgres";
    private static String USERNAME = "pandu";
    private static String PASSWORD = "";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
