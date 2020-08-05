package com.chilke.Sokoban;

//MAN, GOAL, PUSH, DIR
//
//MAN -            64 1000000
//MAN_GOAL -       72 1001000
//MAN_PUSH -       80 1010000
//MAN_GOAL_PUSH -  88 1011000
//
//
//DIR UP 100
//RIGHT 101
//DOWN 110
//LEFT 111

import java.util.Arrays;

enum SkinImage {
    OUTSIDE(0),
    FLOOR(1),
    GOAL(2),
    PACK(3),
    PACK_GOAL(4),
    MAN_MARKER(5),
    PACK_MARKER(6),
    WALL_TOP(7),
    WALL(8),
    BACKGROUND(9),
    WALL_0(16),
    WALL_1(17),
    WALL_2(18),
    WALL_3(19),
    WALL_4(20),
    WALL_5(21),
    WALL_6(22),
    WALL_7(23),
    WALL_8(24),
    WALL_9(25),
    WALL_A(26),
    WALL_B(27),
    WALL_C(28),
    WALL_D(29),
    WALL_E(30),
    WALL_F(31),
    PACK_PULSE_0(32),
    PACK_PULSE_1(33),
    PACK_PULSE_2(34),
    PACK_PULSE_3(35),
    PACK_PULSE_4(36),
    PACK_PULSE_5(37),
    PACK_PULSE_6(38),
    PACK_PULSE_7(39),
    PACK_GOAL_PULSE_0(40),
    PACK_GOAL_PULSE_1(41),
    PACK_GOAL_PULSE_2(42),
    PACK_GOAL_PULSE_3(43),
    PACK_GOAL_PULSE_4(44),
    PACK_GOAL_PULSE_5(45),
    PACK_GOAL_PULSE_6(46),
    PACK_GOAL_PULSE_7(47),
    MAN(64),
    MAN_UP(68),
    MAN_RIGHT(69),
    MAN_DOWN(70),
    MAN_LEFT(71),
    MAN_GOAL(72),
    MAN_GOAL_UP(76),
    MAN_GOAL_RIGHT(77),
    MAN_GOAL_DOWN(78),
    MAN_GOAL_LEFT(79),
    MAN_PUSH(80),
    MAN_PUSH_UP(84),
    MAN_PUSH_RIGHT(85),
    MAN_PUSH_DOWN(86),
    MAN_PUSH_LEFT(87),
    MAN_PUSH_GOAL(88),
    MAN_PUSH_GOAL_UP(92),
    MAN_PUSH_GOAL_RIGHT(93),
    MAN_PUSH_GOAL_DOWN(94),
    MAN_PUSH_GOAL_LEFT(95);

    public final static int MAX_VALUE = 95;
    public final static int PACK_PULSE_BIT = 32;
    public final static int MAN_BIT = 64;
    public final static int GOAL_BIT = 8;
    public final static int PUSH_BIT = 16;
    public final static int DIR_BIT = 4;
    public final static int DIR_UP = 0;
    public final static int DIR_RIGHT = 1;
    public final static int DIR_DOWN = 2;
    public final static int DIR_LEFT = 3;
    public final static int DIR_MASK = DIR_LEFT;
    public final static int DIR_BIT_AND_MASK = 7;
    public final static int WALL_BIT = 16;
    
    public final int value;

    private static final SkinImage[] skinImages = new SkinImage[MAX_VALUE+1];
    public static final SkinImage[] wallImages = new SkinImage[]{WALL_0,WALL_1,WALL_2,WALL_3,WALL_4,WALL_5,WALL_6,WALL_7,WALL_8,WALL_9,WALL_A,WALL_B,WALL_C,WALL_D,WALL_E,WALL_F};
    public static final SkinImage[] manImages = new SkinImage[]{MAN_GOAL,MAN_PUSH_GOAL_UP,MAN_PUSH_GOAL_RIGHT,MAN_PUSH_GOAL_DOWN,MAN_PUSH_GOAL_LEFT,MAN_PUSH_UP,MAN_PUSH_RIGHT,MAN_PUSH_DOWN,MAN_PUSH_LEFT,MAN_GOAL_UP,MAN_GOAL_RIGHT,MAN_GOAL_DOWN,MAN_GOAL_LEFT,MAN_UP,MAN_RIGHT,MAN_DOWN,MAN_LEFT};

    SkinImage(int value) {
        this.value = value;
    }

    public static SkinImage skinImageForValue(int value) {
        return skinImages[value];
    }

    static {
        Arrays.fill(skinImages, null);
        for (SkinImage si : values()) {
            skinImages[si.value] = si;
        }
    }
}
