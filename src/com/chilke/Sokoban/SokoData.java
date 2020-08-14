package com.chilke.Sokoban;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class SokoData {
    private Connection conn = null;

    private static SokoData instance = null;

    public static SokoData getInstance() {
        if (instance == null) {
            try {
                instance = new SokoData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        }

        return instance;
    }

    public SokoData() throws SQLException {
        String url = "jdbc:sqlite:"+getDbPath();
        System.out.println(url);

        conn = DriverManager.getConnection(url);
    }

    public void disconnect() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception ex) {

            }
        }
    }

    private String getDbPath() {
        Path p = Paths.get(System.getProperty("user.home"));
        p = p.resolve("Documents");
        p = p.resolve("Sokoban 3");
        p = p.resolve("Scores");
        p = p.resolve("Sokoban.db");

        return p.toString();
    }

    public int getLocalUserId(String name) {
        String sql = "select Id from Users where Local = 1 and Name = ?";
        int id = -1;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        return id;
    }

//In Scores.Sol, binary data represents LURD move list with the following 2 bit sets
//00 - Up
//01 - Right
//10 - Down
//11 - Left

    public FileData getFile(String name) {
        String sql = "select * from Files where File = ?";
        FileData ret = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ret = new FileData(rs.getInt("Id"), rs.getString("File"),
                        rs.getString("Title"), rs.getInt("Timestamp"),
                        rs.getInt("State"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public FileData insertFile(String name, String title) {
        String sql = "INSERT INTO Files (File, Title) VALUES(?, ?)";
        FileData ret = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, title);

            stmt.execute();

            ret = getFile(name);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public

    public void getScore(int id) {
        String sql = "select Sol from Scores where Id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Found score");
                InputStream stream = rs.getBinaryStream(1);
                byte[] bytes = stream.readAllBytes();
                System.out.println(Utility.bytesToHex(bytes));
            } else {
                System.out.println("Couldn't find score");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
