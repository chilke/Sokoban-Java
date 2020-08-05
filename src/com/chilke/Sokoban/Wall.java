package com.chilke.Sokoban;

public class Wall extends Square {
    private static final int ADJACENT_WALL_NONE = 0;
    private static final int ADJACENT_WALL_UP = 1;
    private static final int ADJACENT_WALL_RIGHT = 2;
    private static final int ADJACENT_WALL_DOWN = 4;
    private static final int ADJACENT_WALL_LEFT = 8;

    private SkinImage skinImage;

    public Wall(int col, int row, Level level) {
        super(col, row, level);

        skinImage = null;
    }

    public boolean needsTop() {
        return level.isWallAt(col, row-1) &&
                level.isWallAt(col-1, row) &&
                level.isWallAt(col-1, row-1);
    }

    public SkinImage getSkinImage() {
        if (skinImage == null) {
            int index = ADJACENT_WALL_NONE;

            if (level.isWallAt(col, row + 1)) {
                index = ADJACENT_WALL_DOWN;
            }
            if (level.isWallAt(col + 1, row)) {
                index |= ADJACENT_WALL_RIGHT;
            }
            if (level.isWallAt(col, row - 1)) {
                index |= ADJACENT_WALL_UP;
            }
            if (level.isWallAt(col - 1, row)) {
                index |= ADJACENT_WALL_LEFT;
            }

            switch (index) {
                case 0:
                    skinImage = SkinImage.WALL_0;
                    break;
                case 1:
                    skinImage = SkinImage.WALL_1;
                    break;
                case 2:
                    skinImage = SkinImage.WALL_2;
                    break;
                case 3:
                    skinImage = SkinImage.WALL_3;
                    break;
                case 4:
                    skinImage = SkinImage.WALL_4;
                    break;
                case 5:
                    skinImage = SkinImage.WALL_5;
                    break;
                case 6:
                    skinImage = SkinImage.WALL_6;
                    break;
                case 7:
                    skinImage = SkinImage.WALL_7;
                    break;
                case 8:
                    skinImage = SkinImage.WALL_8;
                    break;
                case 9:
                    skinImage = SkinImage.WALL_9;
                    break;
                case 10:
                    skinImage = SkinImage.WALL_A;
                    break;
                case 11:
                    skinImage = SkinImage.WALL_B;
                    break;
                case 12:
                    skinImage = SkinImage.WALL_C;
                    break;
                case 13:
                    skinImage = SkinImage.WALL_D;
                    break;
                case 14:
                    skinImage = SkinImage.WALL_E;
                    break;
                case 15:
                    skinImage = SkinImage.WALL_F;
                    break;
                default:
                    skinImage = SkinImage.WALL;
            }
        }

        return skinImage;
    }
}
