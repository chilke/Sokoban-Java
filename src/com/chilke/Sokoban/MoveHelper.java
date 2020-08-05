package com.chilke.Sokoban;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MoveHelper {

    private Level level;
    private Pack pack;

    private ArrayList<PushedPosition> updatedPackPositions;

    private final int[][] scores = new int[150][150];
    private final int[][][] pushScores = new int[150][150][4];
    private final int[][][] moveScores = new int[150][150][4];

    public ArrayList<Move> moveMan(Level level, int col, int row) {
        this.level = level;
        ArrayList<Move> ret = moveMan(col, row);

        if (ret != null) {
            Collections.reverse(ret);
        }

        return ret;
    }

    private ArrayList<Move> moveMan(int col, int row) {
        ArrayList<Move> ret = null;
        Position manPos = new Position(level.getMan().getCol(), level.getMan().getRow());
        Position dest = new Position(col, row);
        if (moveMan(manPos, dest) != -1) {
            int value = scores[dest.getRow()][dest.getCol()];
            ret = new ArrayList<>();

            value--;
            Position p = new Position(dest.getCol(), dest.getRow());
            for (; value >= 0; value--) {
                ret.add(backtrackMove(p, value));
            }
        }

        return ret;
    }

    public ArrayList<Move> movePack(Pack pack, Level level, int col, int row) {
        ArrayList<Move> ret = null;

        this.level = level;
        this.pack = pack;

        Position initialManPosition = new Position(level.getMan().getCol(), level.getMan().getRow());
        Position initialPackPosition = new Position(pack.getCol(), pack.getRow());

        int minPushCnt = movePack(new Position(col, row));

        if (minPushCnt < Integer.MAX_VALUE) {
            ret = new ArrayList<>();

            int i = minPushCnt - 1;
            Direction lastDir;
            Position position = new Position(col, row);
            Move move = backtrackPush(position, i);
            lastDir = move.getDir();
            Position end;

            while (i > 0) {
                i--;
                ret.add(move);
                end = new Position(position.getCol(), position.getRow());
                level.movePack(pack, position.getCol(), position.getRow());
                move = backtrackPush(position, i);

                switch (lastDir) {
                    case DOWN -> end.setRow(end.getRow()-1);
                    case UP -> end.setRow(end.getRow()+1);
                    case LEFT -> end.setCol(end.getCol()+1);
                    case RIGHT -> end.setCol(end.getCol()-1);
                }

                level.moveMan(position.getCol(), position.getRow());
                ret.addAll(moveMan(end.getCol(), end.getRow()));
                lastDir = move.getDir();
            }

            ret.add(move);
            level.movePack(pack, position.getCol(), position.getRow());
            end = new Position(position.getCol(), position.getRow());
            switch (lastDir) {
                case DOWN -> end.setRow(end.getRow()-1);
                case UP -> end.setRow(end.getRow()+1);
                case LEFT -> end.setCol(end.getCol()+1);
                case RIGHT -> end.setCol(end.getCol()-1);
            }
            level.moveMan(initialManPosition.getCol(), initialManPosition.getRow());
            ret.addAll(moveMan(end.getCol(), end.getRow()));

            Collections.reverse(ret);
        }

        level.movePack(pack, initialPackPosition.getCol(), initialPackPosition.getRow());

        return ret;
    }

    private static final MoveHelper instance = new MoveHelper();

    public static MoveHelper getInstance() {
        return instance;
    }

    public ArrayList<Position> getReachable(Level level) {
        this.level = level;

        moveMan(new Position(level.getMan().getCol(), level.getMan().getRow()), null);
        ArrayList<Position> ret = new ArrayList<>();
        for (int row = 0; row < level.getHeight(); row++) {
            for (int col = 0; col < level.getWidth(); col++) {
                if (scores[row][col] != -1) {
                    ret.add(new Position(col, row));
                }
            }
        }

        return ret;
    }

    public ArrayList<Position> getPackReachable(Pack pack, Level level) {
        this.pack = pack;
        this.level = level;

        Position org = new Position(pack.getCol(), pack.getRow());

        movePack(null);

        level.movePack(pack, org.getCol(), org.getRow());

        int[][] reachable = new int[level.getHeight()][level.getWidth()];

        for (int i = 0; i < reachable.length; i++) {
            Arrays.fill(reachable[i], Integer.MAX_VALUE);
        }

        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) {
                continue;
            }
            int addX = 0;
            int addY = 0;
            switch(d) {
                case DOWN -> addY = 1;
                case UP -> addY = -1;
                case LEFT -> addX = -1;
                case RIGHT -> addX = 1;
            }
            for (int j = 0; j < level.getHeight(); j++) {
                for (int i = 0; i < level.getWidth(); i++) {
                    if (pushScores[j][i][d.value] != -1) {
                        reachable[j+addY][i+addX] = Math.min(reachable[j+addY][i+addX], pushScores[j][i][d.value]);
                    }
                }
            }
        }

        ArrayList<Position> ret = new ArrayList<>();

        for (int j = 0; j < level.getHeight(); j++) {
            for (int i = 0; i < level.getWidth(); i++) {
                if (reachable[j][i] != Integer.MAX_VALUE) {
                    ret.add(new Position(i, j));
                }
            }
        }

        return ret;
    }

    private void addUpdatedPackPositions(Position startPos, int pushCnt, int moveCnt) {
        //Start with push up
        Position pushFromPos = new Position(pack.getCol(), pack.getRow()+1);
        if (level.isOpenAt(pack.getCol(), pack.getRow()-1) &&
                moveMan(startPos, pushFromPos) != -1) {
            int curMoves = scores[pushFromPos.getRow()][pushFromPos.getCol()];
            int pushScore = pushScores[pack.getRow()][pack.getCol()][Direction.UP.value];
            int moveScore = moveScores[pack.getRow()][pack.getCol()][Direction.UP.value];
            if (pushScore < 0 || (pushScore == pushCnt && moveScore < moveCnt+curMoves)) {
                updatedPackPositions.add(new PushedPosition(pack.getCol(), pack.getRow(), Direction.UP));
                pushScores[pack.getRow()][pack.getCol()][Direction.UP.value] = pushCnt;
                moveScores[pack.getRow()][pack.getCol()][Direction.UP.value] = moveCnt+curMoves+1;
            }
        }

        //Next try push down
        pushFromPos = new Position(pack.getCol(), pack.getRow()-1);
        if (level.isOpenAt(pack.getCol(), pack.getRow()+1) &&
                moveMan(startPos, pushFromPos) != -1) {
            int curMoves = scores[pushFromPos.getRow()][pushFromPos.getCol()];
            int pushScore = pushScores[pack.getRow()][pack.getCol()][Direction.DOWN.value];
            int moveScore = moveScores[pack.getRow()][pack.getCol()][Direction.DOWN.value];
            if (pushScore < 0 || (pushScore == pushCnt && moveScore < moveCnt+curMoves)) {
                updatedPackPositions.add(new PushedPosition(pack.getCol(), pack.getRow(), Direction.DOWN));
                pushScores[pack.getRow()][pack.getCol()][Direction.DOWN.value] = pushCnt;
                moveScores[pack.getRow()][pack.getCol()][Direction.DOWN.value] = moveCnt+curMoves+1;
            }
        }

        //And then left
        pushFromPos = new Position(pack.getCol()+1, pack.getRow());
        if (level.isOpenAt(pack.getCol()-1, pack.getRow()) &&
                moveMan(startPos, pushFromPos) != -1) {
            int curMoves = scores[pushFromPos.getRow()][pushFromPos.getCol()];
            int pushScore = pushScores[pack.getRow()][pack.getCol()][Direction.LEFT.value];
            int moveScore = moveScores[pack.getRow()][pack.getCol()][Direction.LEFT.value];
            if (pushScore < 0 || (pushScore == pushCnt && moveScore < moveCnt+curMoves)) {
                updatedPackPositions.add(new PushedPosition(pack.getCol(), pack.getRow(), Direction.LEFT));
                pushScores[pack.getRow()][pack.getCol()][Direction.LEFT.value] = pushCnt;
                moveScores[pack.getRow()][pack.getCol()][Direction.LEFT.value] = moveCnt+curMoves+1;
            }
        }

        //And finally right
        pushFromPos = new Position(pack.getCol()-1, pack.getRow());
        if (level.isOpenAt(pack.getCol()+1, pack.getRow()) &&
                moveMan(startPos, pushFromPos) != -1) {
            int curMoves = scores[pushFromPos.getRow()][pushFromPos.getCol()];
            int pushScore = pushScores[pack.getRow()][pack.getCol()][Direction.RIGHT.value];
            int moveScore = moveScores[pack.getRow()][pack.getCol()][Direction.RIGHT.value];
            if (pushScore < 0 || (pushScore == pushCnt && moveScore < moveCnt+curMoves)) {
                updatedPackPositions.add(new PushedPosition(pack.getCol(), pack.getRow(), Direction.RIGHT));
                pushScores[pack.getRow()][pack.getCol()][Direction.RIGHT.value] = pushCnt;
                moveScores[pack.getRow()][pack.getCol()][Direction.RIGHT.value] = moveCnt+curMoves+1;
            }
        }
    }

    private Move backtrackMove(Position p, int value) {
        Direction dir = Direction.NONE;

        if (p.getRow() > 0 && scores[p.getRow()-1][p.getCol()] == value) {
            dir = Direction.DOWN;
            p.setRow(p.getRow()-1);
        } else if (p.getRow() < level.getHeight()-1 && scores[p.getRow()+1][p.getCol()] == value) {
            dir = Direction.UP;
            p.setRow(p.getRow()+1);
        } else if (p.getCol() > 0 && scores[p.getRow()][p.getCol()-1] == value) {
            dir = Direction.RIGHT;
            p.setCol(p.getCol()-1);
        } else if (p.getCol() < level.getWidth()-1 && scores[p.getRow()][p.getCol()+1] == value) {
            dir = Direction.LEFT;
            p.setCol(p.getCol()+1);
        } else {
            System.err.println("Uh-oh, backtracking move and we got stuck");
        }

        return new Move(dir);
    }

    private Move backtrackPush(Position p, int value) {
        Direction dir = Direction.NONE;
        int minMoves = Integer.MAX_VALUE;

        if (p.getRow() > 0 && pushScores[p.getRow()-1][p.getCol()][Direction.DOWN.value] == value &&
            moveScores[p.getRow()-1][p.getCol()][Direction.DOWN.value] < minMoves) {
            dir = Direction.DOWN;
            minMoves = moveScores[p.getRow()-1][p.getCol()][Direction.DOWN.value];
        }
        if (p.getRow() < level.getHeight()-1 && pushScores[p.getRow()+1][p.getCol()][Direction.UP.value] == value &&
                moveScores[p.getRow()+1][p.getCol()][Direction.UP.value] < minMoves) {
            dir = Direction.UP;
            minMoves = moveScores[p.getRow()+1][p.getCol()][Direction.UP.value];
        }
        if (p.getCol() > 0 && pushScores[p.getRow()][p.getCol()-1][Direction.RIGHT.value] == value &&
                moveScores[p.getRow()][p.getCol()-1][Direction.RIGHT.value] < minMoves) {
            dir = Direction.RIGHT;
            minMoves = moveScores[p.getRow()][p.getCol()-1][Direction.RIGHT.value];
        }
        if (p.getCol() < level.getWidth()-1 && pushScores[p.getRow()][p.getCol()+1][Direction.LEFT.value] == value &&
                moveScores[p.getRow()][p.getCol()+1][Direction.LEFT.value] < minMoves) {
            dir = Direction.LEFT;
            //This is unneccesary as there are no checks after this point
//            minMoves = moveScores[p.getRow()][p.getCol()+1][Direction.LEFT.value];
        }

        switch (dir) {
            case DOWN -> p.setRow(p.getRow()-1);
            case UP -> p.setRow(p.getRow()+1);
            case LEFT -> p.setCol(p.getCol()+1);
            case RIGHT -> p.setCol(p.getCol()-1);
            case NONE -> System.err.println("Uh-oh, major problem in backtrackPush");
        }

        return new Move(dir, pack);
    }

    private int movePack(Position dest) {
        for (int i = 0; i < level.getHeight(); i++) {
            for (int j = 0; j < level.getWidth(); j++) {
                Arrays.fill(pushScores[i][j], -1);
            }
        }

        updatedPackPositions = new ArrayList<>();

        addUpdatedPackPositions(new Position(level.getMan().getCol(), level.getMan().getRow()), 0, 0);

        int minPushCnt = Integer.MAX_VALUE;
        int minMovesCnt = Integer.MAX_VALUE;
        int count = 1;
        int moves = 0;

        Position start = new Position(0, 0);

        while (!updatedPackPositions.isEmpty() && minPushCnt == Integer.MAX_VALUE) {
            ArrayList<PushedPosition> packPositions = updatedPackPositions;
            updatedPackPositions = new ArrayList<>();

            for (PushedPosition p : packPositions) {
                moves = moveScores[p.getRow()][p.getCol()][p.getDir().value];
                start.setCol(p.getCol());
                start.setRow(p.getRow());
                switch (p.getDir()) {
                    case RIGHT -> p.setCol(p.getCol() + 1);
                    case LEFT -> p.setCol(p.getCol() - 1);
                    case UP -> p.setRow(p.getRow() - 1);
                    case DOWN -> p.setRow(p.getRow() + 1);
                }

                if (dest != null && p.getCol() == dest.getCol() && p.getRow() == dest.getRow()) {
                    minPushCnt = count;
                    if (moves < minMovesCnt) {
                        minMovesCnt = moves;
                    }
                }
                level.movePack(pack, p.getCol(), p.getRow());
                addUpdatedPackPositions(start, count, moves);
            }
            count++;
        }

        return minPushCnt;
    }

    private int moveMan(Position source, Position dest) {
        for (int row = 0; row < level.getHeight(); row++) {
            for (int col = 0; col < level.getWidth(); col++) {
                scores[row][col] = -1;
            }
        }

        ArrayList<Position> updatedPositions = new ArrayList<>();
        updatedPositions.add(source);
        scores[source.getRow()][source.getCol()] = 0;
        int count = 0;
        while (!updatedPositions.isEmpty() && (dest == null || (scores[dest.getRow()][dest.getCol()] == -1))) {
            count++;
            ArrayList<Position> currentPositions = updatedPositions;
            updatedPositions = new ArrayList<>();
            for (Position p : currentPositions) {
                if (checkMove(p.getCol()+1, p.getRow())) {
                    scores[p.getRow()][p.getCol()+1] = count;
                    updatedPositions.add(new Position(p.getCol()+1, p.getRow()));
                }
                if (checkMove(p.getCol()-1, p.getRow())) {
                    scores[p.getRow()][p.getCol()-1] = count;
                    updatedPositions.add(new Position(p.getCol()-1, p.getRow()));
                }
                if (checkMove(p.getCol(), p.getRow()+1)) {
                    scores[p.getRow()+1][p.getCol()] = count;
                    updatedPositions.add(new Position(p.getCol(), p.getRow()+1));
                }
                if (checkMove(p.getCol(), p.getRow()-1)) {
                    scores[p.getRow()-1][p.getCol()] = count;
                    updatedPositions.add(new Position(p.getCol(), p.getRow()-1));
                }
            }
        }

        return dest == null ? -1 : scores[dest.getRow()][dest.getCol()];
    }

    private boolean checkMove(int col, int row) {
        return col >= 0 && row >= 0 && col < level.getWidth() && row < level.getHeight() &&
                scores[row][col] == -1 && level.isOpenAt(col, row);
    }
}
