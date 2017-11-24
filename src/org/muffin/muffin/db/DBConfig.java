package org.muffin.muffin.db;

public class DBConfig {
    public static String URL = "jdbc:postgresql://localhost:5730/postgres";
    public static String USERNAME = "harsha";
    public static String PASSWORD = "";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}