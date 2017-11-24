package org.muffin.muffin.db;

public class DBConfig {
    public static String URL = "jdbc:postgresql://localhost:5081/postgres";
    public static String USERNAME = "pandu";
    public static String PASSWORD = "";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}