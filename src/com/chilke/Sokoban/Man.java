package com.chilke.Sokoban;

public class Man extends Square {
    private static final Move noMove = new Move();

    public Man(int col, int row, Level level) {
        super(col, row, level);
    }

    public SkinImage getSkinImage() {
        return getSkinImage(noMove);
    }

    public SkinImage getSkinImage(Move m) {
        if (m == null) {
            m = noMove;
        }
        int skinImageValue = SkinImage.MAN.value;

        if (level.isGoalAt(col, row)) {
            skinImageValue |= SkinImage.GOAL_BIT;
        }

        if (m.isPush()) {
            skinImageValue |= SkinImage.PUSH_BIT;
        }

        if (m.getDir() != Direction.NONE) {
            skinImageValue |= SkinImage.DIR_BIT;
            switch(m.getDir()) {
                case LEFT:
                    skinImageValue |= SkinImage.DIR_LEFT;
                    break;
                case DOWN:
                    skinImageValue |= SkinImage.DIR_DOWN;
                    break;
                case RIGHT:
                    skinImageValue |= SkinImage.DIR_RIGHT;
                    break;
                case UP:
                    skinImageValue |= SkinImage.DIR_UP;
                    break;
            }
        }

        return SkinImage.skinImageForValue(skinImageValue);
    }
}
