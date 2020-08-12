package com.chilke.Sokoban;

import java.sql.SQLException;

public class DataTest {
    public static void main(String[] args) {
        SokoData data = null;

        try {
            data = new SokoData();
            System.out.println("Connection Successful");
            int id = data.getLocalUserId("Craig");
            System.out.format("Id for Craig is %d\n", id);
            data.getScore(2613);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (data != null) {
                data.disconnect();
            }
        }
    }
}
