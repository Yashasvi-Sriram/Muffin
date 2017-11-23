package org.muffin.muffin.daoimplementations;

import org.muffin.muffin.daos.SeatDAO;
import org.muffin.muffin.db.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SeatDAOImpl implements SeatDAO {
    @Override
    public boolean createList(int theatreID, List<Integer> x, List<Integer> y) {

        try (Connection conn = DriverManager.getConnection(DBConfig.URL, DBConfig.USERNAME, DBConfig.PASSWORD);) {
            PreparedStatement preparedStmt = conn.prepareStatement("INSERT INTO seat(theatre_id,x,y) VALUES(?,?,?);");
            if(x != null && y != null && x.size() == y.size()) {
                conn.setAutoCommit(false);
                for(int i=0;i < x.size();i++) {
                    preparedStmt.setInt(1,theatreID);
                    preparedStmt.setInt(2,x.get(i));
                    preparedStmt.setInt(3,y.get(i));
                    preparedStmt.executeQuery();
                }
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
