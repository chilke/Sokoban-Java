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
            data.getScore(2617);
            String lurd = "ULLLUUULULLDLLDDDRRRRRRRRRRRRRDRULLLLLLLLLLLLLLULLDRRRRRRRRRRRRRRURDLDRLLULLLLLLUUULULLDDDUULLDDDRRRRRRRRRRRRDRULLLLLLLLUUULLULDDDUULLDDDRRRRRRRRRRRURDLDRLULLLLLLUUULLUUURDDLUULDDDDDUULLDDDRRRRRRRRRRRRRLLLLLLLLUUULLULDDDUULLDDDRRRRRRRRRRRR";
            byte[] bytes = Utility.lurdToBytes(lurd);
            String lurd2 = Utility.bytesToLurd(bytes, lurd.length());
            System.out.println(Utility.bytesToHex(bytes));
            System.out.println(lurd);
            System.out.println(lurd2);
//            data.createScore("Test", "ULLLUUULULLDLLDDDRRRRRRRRRRRRRDRULLLLLLLLLLLLLLULLDRRRRRRRRRRRRRRURDLDRLLULLLLLLUUULULLDDDUULLDDDRRRRRRRRRRRRDRULLLLLLLLUUULLULDDDUULLDDDRRRRRRRRRRRURDLDRLULLLLLLUUULLUUURDDLUULDDDDDUULLDDDRRRRRRRRRRRRRLLLLLLLLUUULLULDDDUULLDDDRRRRRRRRRRRR",
//                    239, 100);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            if (data != null) {
                data.disconnect();
            }
        }
    }
}
