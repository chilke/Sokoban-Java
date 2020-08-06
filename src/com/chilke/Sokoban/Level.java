package com.chilke.Sokoban;

import java.util.ArrayList;

public class Level {
    private final static char FLOOR_CHAR = ' ';
    private final static char WALL_CHAR = '#';
    private final static char PACK_CHAR = '$';
    private final static char GOAL_CHAR = '.';
    private final static char PACK_GOAL_CHAR = '*';
    private final static char MAN_CHAR = '@';
    private final static char MAN_GOAL_CHAR = '+';

    private final String name;
    private final int width;
    private final int height;
    private final int[][] grid;

    private final ArrayList<Pack> packs = new ArrayList();
    private final ArrayList<Square> goals = new ArrayList();
    private final ArrayList<Square> floors = new ArrayList();
    private final ArrayList<Wall> walls = new ArrayList();
    private Man man;

    private int moves;
    private int pushes;

    private boolean complete;

    private ArrayList<ArrayList<Move>> movesLists = new ArrayList<>();
    private ArrayList<Move> movesList = new ArrayList<>();

    public Level(String name, int width, int height, String[] startGrid) {
        this.name = name;
        this.width = width;
        this.height = height;
        complete = false;

        grid = new int[height][width];

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                char c = ' ';

                if (i < startGrid[j].length()) {
                    c = startGrid[j].charAt(i);
                }

                grid[j][i] = getSquareType(c);

                if ((grid[j][i] & Square.MAN) != 0) {
                    man = new Man(i, j, this);
                }
                if ((grid[j][i] & Square.GOAL) != 0) {
                    Square goal = new Square(i, j, this);
                    goals.add(goal);

                }
                if ((grid[j][i] & Square.WALL) != 0) {
                    Wall wall = new Wall(i, j, this);
                    walls.add(wall);
                    floors.add(new Square(i, j, this));
                }
//                if ((grid[j][i] & Square.FLOOR) != 0) {
//                    Square floor = new Square(i, j, this);
//                    floors.add(floor);
//                }
                if ((grid[j][i] & Square.PACK) != 0) {
                    Pack pack = new Pack(i, j, this);
                    packs.add(pack);
                }
            }
        }
        //Temporarily remove packs to find reachable areas of the level
        for (Pack p : packs) {
            grid[p.getRow()][p.getCol()] &= ~Square.PACK;
        }

        ArrayList<Position> reachable = MoveHelper.getInstance().getReachable(this);
        for (Position p : reachable) {
            floors.add(new Square(p.getCol(), p.getRow(), this));
        }

        //Now put the packs back in place
        for (Pack p : packs) {
            grid[p.getRow()][p.getCol()] |= Square.PACK;
        }
    }

    public Move getMove(Direction dir) {
        int curManCol = man.getCol();
        int curManRow = man.getRow();
        int newManCol = curManCol;
        int newManRow = curManRow;
        int newPackCol = curManCol;
        int newPackRow = curManRow;

        switch (dir) {
            case LEFT:
                newManCol--;
                newPackCol-=2;
                break;
            case RIGHT:
                newManCol++;
                newPackCol+=2;
                break;
            case UP:
                newManRow--;
                newPackRow-=2;
                break;
            case DOWN:
                newManRow++;
                newPackRow+=2;
                break;
            case NONE:
                return null;
        }

        if (isOpenAt(newManCol, newManRow)) {
            return new Move(dir);
        } else {
            Pack p = packAt(newManCol, newManRow);
            if (p != null && isOpenAt(newPackCol, newPackRow)) {
                return new Move(dir, p);
            }
        }

        return null;
    }

    public Move getLastMove() {
        Move ret = null;
        if (!movesLists.isEmpty()) {
            ArrayList<Move> last = movesLists.get(movesLists.size()-1);
            if (!last.isEmpty()) {
                ret = last.remove(last.size()-1);
            } else {
                movesLists.remove(movesLists.size()-1);
            }
        }

        return ret;
    }

    public void endMove() {
        movesLists.add(movesList);
        movesList = new ArrayList<>();
    }

    public void doMove(Move m) {
//        System.out.format("Moving from (%d, %d) in %s\n", man.getCol(), man.getRow(), m.getDir().toString());
        Pack p = m.getPushedPack();
        switch (m.getDir()) {
            case LEFT:
                moveMan(man.getCol()-1, man.getRow());
                if (p != null) {
                    movePack(p, p.getCol()-1, p.getRow());
                }
                break;
            case RIGHT:
                moveMan(man.getCol()+1, man.getRow());
                if (p != null) {
                    movePack(p, p.getCol()+1, p.getRow());
                }
                break;
            case UP:
                moveMan(man.getCol(), man.getRow()-1);
                if (p != null) {
                    movePack(p, p.getCol(), p.getRow()-1);
                }
                break;
            case DOWN:
                moveMan(man.getCol(), man.getRow()+1);
                if (p != null) {
                    movePack(p, p.getCol(), p.getRow()+1);
                }
                break;
        }

        moves++;
        if (p != null) {
            pushes++;
        }

        movesList.add(m);
    }

    public void undoMove(Move m) {
//        System.out.format("Moving from (%d, %d) in %s\n", man.getCol(), man.getRow(), m.getDir().toString());
        Pack p = m.getPushedPack();
        switch (m.getDir()) {
            case LEFT:
                moveMan(man.getCol()+1, man.getRow());
                if (p != null) {
                    movePack(p, p.getCol()+1, p.getRow());
                }
                break;
            case RIGHT:
                moveMan(man.getCol()-1, man.getRow());
                if (p != null) {
                    movePack(p, p.getCol()-1, p.getRow());
                }
                break;
            case UP:
                moveMan(man.getCol(), man.getRow()+1);
                if (p != null) {
                    movePack(p, p.getCol(), p.getRow()+1);
                }
                break;
            case DOWN:
                moveMan(man.getCol(), man.getRow()-1);
                if (p != null) {
                    movePack(p, p.getCol(), p.getRow()-1);
                }
                break;
        }

        moves--;
        if (p != null) {
            pushes--;
        }
    }

    public void moveMan(int col, int row) {
        grid[man.getRow()][man.getCol()] &= ~Square.MAN;
        grid[row][col] |= Square.MAN;
        man.setCol(col);
        man.setRow(row);
    }

    public void movePack(Pack p, int col, int row) {
        grid[p.getRow()][p.getCol()] &= ~Square.PACK;
        grid[row][col] |= Square.PACK;
        p.setCol(col);
        p.setRow(row);
        complete = true;
        for (Pack p2 : packs) {
            if (!isGoalAt(p2.getCol(), p2.getRow())) {
                complete = false;
                break;
            }
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isWallAt(int col, int row) {
        if (row < 0 || col < 0 || row >= height || col >= width) {
            return false;
        }
        return (grid[row][col] & Square.WALL) != 0;
    }

    public boolean isOutsideAt(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            return true;
        }

        return grid[row][col] == Square.OUTSIDE;
    }

    public boolean isOpenAt(int col, int row) {
        if (col < 0 || col >= width || row < 0 || row >= height) {
            return false;
        }
        else if ((grid[row][col] & Square.WALL) != 0 || (grid[row][col] & Square.PACK) != 0) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean isGoalAt(int col, int row) {
        return ((grid[row][col] & Square.GOAL) > 0);
    }

    public Pack packAt(int col, int row) {
        Pack pack = null;

        if ((grid[row][col] & Square.PACK) > 0) {
            for (Pack p : packs) {
                if (p.getCol() == col && p.getRow() == row) {
                    pack = p;
                    break;
                }
            }
        }

        return pack;
    }

    private static int getSquareType(char c) {
        int value = Square.OUTSIDE;
        switch (c) {
            case FLOOR_CHAR:
                value = Square.FLOOR;
                break;
            case WALL_CHAR:
                value = Square.WALL | Square.FLOOR;
                break;
            case PACK_CHAR:
                value = Square.PACK | Square.FLOOR;
                break;
            case GOAL_CHAR:
                value = Square.GOAL;
                break;
            case PACK_GOAL_CHAR:
                value = Square.GOAL | Square.PACK;
                break;
            case MAN_CHAR:
                value = Square.MAN | Square.FLOOR;
                break;
            case MAN_GOAL_CHAR:
                value = Square.MAN | Square.GOAL;
                break;
        }

        return value;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Pack> getPacks() {
        return packs;
    }

    public ArrayList<Square> getGoals() {
        return goals;
    }

    public ArrayList<Square> getFloors() {
        return floors;
    }

    public ArrayList<Wall> getWalls() {
        return walls;
    }

    public Man getMan() {
        return man;
    }

    public int getMoves() {
        return moves;
    }

    public int getPushes() {
        return pushes;
    }
}
