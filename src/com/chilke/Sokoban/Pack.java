package com.chilke.Sokoban;

public class Pack extends Square {

    public Pack(int col, int row, Level level) {
        super(col, row, level);
    }

    public SkinImage getSkinImage() {
        if (level.isGoalAt(col, row)) {
            return SkinImage.PACK_GOAL;
        }
        return SkinImage.PACK;
    }

    public SkinImage getPulseSkinImage(int value) {
        if (value <= 0) {
            return getSkinImage();
        }
        SkinImage base = SkinImage.PACK_PULSE_0;
        if (level.isGoalAt(col, row)) {
            base = SkinImage.PACK_GOAL_PULSE_0;
        }

        return SkinImage.skinImageForValue(base.value+value-1);
    }
}
