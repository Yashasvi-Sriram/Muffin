package org.muffin.muffin.db;

import com.google.gson.*;

import java.sql.*;

import javax.servlet.http.HttpServletRequest;

public class Utils {
    private static JsonArray resultSetToJsonArray(ResultSet rs) throws SQLException {
        JsonArray json = new JsonArray();
        ResultSetMetaData rsMetaData = rs.getMetaData();
        while (rs.next()) {
            int noColumns = rsMetaData.getColumnCount();
            JsonObject obj = new JsonObject();
            for (int i = 1; i < noColumns + 1; i++) {
                String columnName = rsMetaData.getColumnName(i);

                if (rsMetaData.getColumnType(i) == Types.BIGINT) {
                    obj.addProperty(columnName, rs.getInt(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.BOOLEAN) {
                    obj.addProperty(columnName, rs.getBoolean(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.DOUBLE) {
                    obj.addProperty(columnName, rs.getDouble(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.FLOAT) {
                    obj.addProperty(columnName, rs.getFloat(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.INTEGER) {
                    obj.addProperty(columnName, rs.getInt(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.NVARCHAR) {
                    obj.addProperty(columnName, rs.getNString(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.VARCHAR) {
                    obj.addProperty(columnName, rs.getString(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.TINYINT) {
                    obj.addProperty(columnName, rs.getInt(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.SMALLINT) {
                    obj.addProperty(columnName, rs.getInt(columnName));
                } else if (rsMetaData.getColumnType(i) == Types.TIMESTAMP) {
                    obj.addProperty(columnName, rs.getTimestamp(columnName).getTime());
                } else if (rsMetaData.getColumnType(i) == Types.DATE) {
                    obj.addProperty(columnName, rs.getDate(columnName).getTime());
                } else if (rsMetaData.getColumnType(i) == Types.BLOB) {
                    throw new JsonIOException("BLOB serialization is not available");
                } else if (rsMetaData.getColumnType(i) == Types.ARRAY) {
                    throw new JsonIOException("Array serialization is not available");
                } else {
                    throw new JsonIOException("Unknown column format found");
                }
            }
        }
        return json;
    }
}