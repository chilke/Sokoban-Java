package com.chilke.Sokoban;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class SokoData {
    private final Connection conn;

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
                ex.printStackTrace();
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

    public ArrayList<LevelData> getLevels(int fileId) {
        ArrayList<LevelData> ret = new ArrayList<>();
        String sql = "select Id, File, Nr, Hash, Solved, sum(User) as ScoresSolved from (" +
                    "select distinct l.Id, l.File, l.Nr, l.Hash, l.Solved, " +
                    "case when s.User is null then 0 else 1<<(s.User-1) end as User " +
                    "from Levels l left join Scores s on l.Hash = s.Hash and s.User < 100 " +
                    "where File = ?) " +
                "group by Id, File, Nr, Hash";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fileId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LevelData ld = new LevelData(rs.getInt("Id"), rs.getInt("File"),
                        rs.getInt("Nr"), rs.getString("Hash"),
                        rs.getInt("Solved"), rs.getInt("ScoresSolved"));

                if (ld.getNumber() < ret.size()) {
                    ret.set(ld.getNumber(), ld);
                } else {
                    while (ld.getNumber() > ret.size()) {
                        ret.add(null);
                    }

                    ret.add(ld);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public LevelData getLevel(int fileId, int index) {
        String sql = "select Id, File, Nr, Hash, Solved, sum(User) as ScoresSolved from (" +
                "select distinct l.Id, l.File, l.Nr, l.Hash, l.Solved, " +
                "case when s.User is null then 0 else 1<<(s.User-1) end as User " +
                "from Levels l left join Scores s on l.Hash = s.Hash and s.User < 100 " +
                "where File = ? and Nr = ?) " +
                "group by Id, File, Nr, Hash";
        LevelData ret = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fileId);
            stmt.setInt(2, index);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ret = new LevelData(rs.getInt("Id"), rs.getInt("File"),
                        rs.getInt("Nr"), rs.getString("Hash"),
                        rs.getInt("Solved"), rs.getInt("ScoresSolved"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public LevelData createLevel(int fileId, int index, String hash) {
        String sql = "insert into Levels(File, Nr, Hash) values(?, ?, ?)";
        LevelData ret = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fileId);
            stmt.setInt(2, index);
            stmt.setString(3, hash);

            int row = stmt.executeUpdate();
            if (row == 1) {
                ret = getLevel(fileId, index);
                if (ret.getSolvedMask() != ret.getScoresSolvedMask()) {
                    updateLevelSolved(ret.getId(), ret.getScoresSolvedMask());
                    ret.setSolvedMask(ret.getScoresSolvedMask());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    public void updateLevelSolved(int id, int solved) {
        String sql = "update Levels set Solved = ? where Id = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, solved);
            stmt.setInt(2, id);
            stmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteLevel(int fileId, int index) {
        String sql = "delete from Levels where File = ? and Nr = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, fileId);
            stmt.setInt(2, index);
            stmt.execute();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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
