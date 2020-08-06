package com.chilke.Sokoban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LevelPanel extends JPanel {
    private static final int DEFAULT_SIZE = 30;

    private Level level;
    private Skin skin;
    private int skinSize;

    private int bgOffsetX;
    private int bgOffsetY;

    private int levelOffsetX;
    private int levelOffsetY;

    private int moveEndTime = 500;

    private int frameTime = 1000/60;
    private int moveAnimationMs = 20;
    private long moveStartTime;
    private int currentMoveX = 0;
    private int currentMoveY = 0;
    private Pack pushedPack = null;
    private boolean hasMoved;

    private ArrayList<Move> moves = null;
    private int movesIndex;
    private Move currentMove = null;

    private Timer animationTimer;
    private Timer moveEndTimer;

    private BufferedImage bgImage;
    private BufferedImage levelImage;

    private Direction lastDirection = Direction.LEFT;

    private ArrayList<Position> manMarkers = null;
    private ArrayList<Position> packMarkers = null;
    private Pack selectedPack = null;

    private long lastPulseTime;
    private int pulseStepTime = 75;
    private int currentPulseId = 0;
    private boolean pulseIncreasing = true;

    private boolean isUndoing = false;

    private SokobanApp app;

    public LevelPanel(SokobanApp app) {
        this.app = app;
        level = null;
        skin = null;
        skinSize = DEFAULT_SIZE;

        animationTimer = new Timer(frameTime, animationAction);
        moveEndTimer = new Timer(moveEndTime, moveEndAction);

        setFocusable(true);
        addKeyListener(keyAdapter);
        addMouseListener(mouseAdapter);
        AbstractAction doNothing = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                drawBackground();
                drawStaticLevel();
            }
        });
    }

    public Level getLevel() {
        return level;
    }

    private void drawBackground() {
        if (getWidth() > 0 && getHeight() > 0 && skin != null) {
            bgImage = new BufferedImage(getWidth()+2*skinSize, getHeight()+2*skinSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bgImage.createGraphics();

            BufferedImage img;
            if (skin.hasBackground()) {
                img = skin.getSkinImage(SkinImage.BACKGROUND);
            } else {
                img = skin.getSkinImage(SkinImage.OUTSIDE);
            }

            int imgWidth = img.getWidth();
            int imgHeight = img.getHeight();

            int x;
            int y = 0;

            while (y < bgImage.getHeight()) {
                x = 0;
                while (x < bgImage.getWidth()) {
                    g.drawImage(img, x, y, null);
                    x += imgWidth;
                }
                y += imgHeight;
            }

            g.dispose();

            repaint();
        }
    }

    public void drawStaticLevel() {
        if (skin != null && level != null) {
            levelImage = new BufferedImage(level.getWidth()*skinSize, level.getHeight()*skinSize,
                    BufferedImage.TYPE_INT_ARGB);

            setPreferredSize(new Dimension(levelImage.getWidth()+skinSize,
                    levelImage.getHeight()+skinSize));
            revalidate();
            Graphics2D g = levelImage.createGraphics();
            BufferedImage floorImage = skin.getSkinImage(SkinImage.FLOOR);
            for (Square floor : level.getFloors()) {
                g.drawImage(floorImage, floor.getCol()*skinSize, floor.getRow()*skinSize, null);
            }

            BufferedImage goalImage = skin.getSkinImage(SkinImage.GOAL);
            for (Square goal : level.getGoals()) {
                g.drawImage(goalImage, goal.getCol()*skinSize, goal.getRow()*skinSize, null);
            }

            BufferedImage wallTop = skin.getSkinImage(SkinImage.WALL_TOP);
            for (Wall w : level.getWalls()) {
                BufferedImage img = skin.getSkinImage(w.getSkinImage());
                g.drawImage(img, w.getCol()*skinSize, w.getRow()*skinSize, null);

                if (wallTop != null && w.needsTop()) {
                    g.drawImage(wallTop, (w.getCol()-1)*skinSize+skin.getWallTopX(),
                            (w.getRow()-1)*skinSize+skin.getWallTopY(), null);
                }
            }

            g.dispose();

            int extraW = getWidth() - levelImage.getWidth();
            levelOffsetX = extraW/2;
            int extraH = getHeight() - levelImage.getHeight();
            levelOffsetY = extraH/2;
            if (skin.hasBackground()) {
                bgOffsetX = 0;
                bgOffsetY = 0;
            } else {
                bgOffsetX = levelOffsetX%skinSize - skinSize;
                bgOffsetY = levelOffsetY%skinSize - skinSize;
            }
        }
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        this.skin.setSkinSize(skinSize);

        drawBackground();
        drawStaticLevel();
    }

    public void setLevel(Level level) {
        this.level = level;
        manMarkers = null;
        packMarkers = null;
        selectedPack = null;
        drawStaticLevel();
        repaint();
    }

    public int setSize(int size) {
        skinSize = skin.setSkinSize(size);
        drawBackground();
        drawStaticLevel();
        return skinSize;
    }

    public int increaseSize() {
        return setSize(skinSize+1);
    }

    public int decreaseSize() {
        return setSize(skinSize-1);
    }

    private void paintPlayPieces(Graphics g) {
        Man m = level.getMan();
        SkinImage skinImage = m.getSkinImage(currentMove);
        BufferedImage img = skin.getSkinImage(skinImage);
        if (img == null) {
            if ((skinImage.value & SkinImage.DIR_BIT) == 0) {
                skinImage = SkinImage.skinImageForValue(skinImage.value | SkinImage.DIR_BIT);
                img = skin.getSkinImage(skinImage);
            } else {
                System.out.println("Missing man image: "+skinImage.toString());
            }
        }
        g.drawImage(img, m.getCol()*skinSize+levelOffsetX+currentMoveX,
                m.getRow()*skinSize+levelOffsetY+currentMoveY, this);

        for (Pack p : level.getPacks()) {
            int x = p.getCol()*skinSize+levelOffsetX;
            int y = p.getRow()*skinSize+levelOffsetY;

            if (p == pushedPack) {
                x += currentMoveX;
                y += currentMoveY;
            }

            if (selectedPack == p) {
                img = skin.getSkinImage(p.getPulseSkinImage(currentPulseId));
            } else {
                img = skin.getSkinImage(p.getSkinImage());
            }
            g.drawImage(img, x, y, this);

        }

        if (manMarkers != null) {
            img = skin.getSkinImage(SkinImage.MAN_MARKER);
            for (Position p : manMarkers) {
                if (p.getCol() != level.getMan().getCol() || p.getRow() != level.getMan().getRow()) {
                    g.drawImage(img, p.getCol()*skinSize+levelOffsetX, p.getRow()*skinSize+levelOffsetY, this);
                }
            }
        }

        if (packMarkers != null) {
            img = skin.getSkinImage(SkinImage.PACK_MARKER);
            for (Position p : packMarkers) {
                if (p.getCol() != selectedPack.getCol() || p.getRow() != selectedPack.getRow()) {
                    g.drawImage(img, p.getCol()*skinSize+levelOffsetX, p.getRow()*skinSize+levelOffsetY, this);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (skin != null && level != null) {
            g.drawImage(bgImage, bgOffsetX, bgOffsetY, this);

            g.drawImage(levelImage, levelOffsetX, levelOffsetY, this);

            paintPlayPieces(g);
        } else {
            g.setColor(new Color(127, 0, 127));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    private void animateMoves() {
        movesIndex = 0;
        hasMoved = false;
        moveStartTime = System.currentTimeMillis();
        currentMove = moves.get(0);
        animationTimer.start();
    }

    private void undoMove() {
        currentMove = level.getLastMove();
        if (currentMove != null) {
            hasMoved = false;
            moveStartTime = System.currentTimeMillis();
            isUndoing = true;
            animationTimer.start();
        }
    }

    private ActionListener animationAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (moves != null) {
                moveEndTimer.stop();
                long currentTime = System.currentTimeMillis();
                int offsetTime = (int) (currentTime - moveStartTime);

                while (offsetTime >= moveAnimationMs && currentMove != null) {
                    if (!hasMoved) {
                        level.doMove(currentMove);
                        app.updateMoves();
                    }
                    currentMoveX = 0;
                    currentMoveY = 0;
                    hasMoved = false;
                    movesIndex++;
                    if (movesIndex >= moves.size()) {
                        currentMove = null;
                    } else {
                        currentMove = moves.get(movesIndex);
                    }
                    moveStartTime += moveAnimationMs;
                    offsetTime = (int)(currentTime - moveStartTime);
                }
                if (currentMove == null) {
                    level.endMove();
                    moves = null;
                    animationTimer.stop();
                    if (moveAnimationMs > 0) {
                        moveEndTimer.start();
                    }
                } else {
                    pushedPack = currentMove.getPushedPack();
                    int offsetDistance = (offsetTime * skinSize) / moveAnimationMs;
                    if (offsetDistance > skinSize/2) {
                        if (!hasMoved) {
                            level.doMove(currentMove);
                            app.updateMoves();
                            hasMoved = true;
                        }
                        offsetDistance -= skinSize;
                    }

                    switch (currentMove.getDir()) {
                        case UP -> currentMoveY = -offsetDistance;
                        case DOWN -> currentMoveY = offsetDistance;
                        case LEFT -> currentMoveX = -offsetDistance;
                        case RIGHT -> currentMoveX = offsetDistance;
                    }
                }
            } else if (isUndoing) {
                moveEndTimer.stop();

                long currentTime = System.currentTimeMillis();

                int offsetTime = (int)(currentTime - moveStartTime);

                while (offsetTime >= moveAnimationMs && currentMove != null) {
                    if (!hasMoved) {
                        level.undoMove(currentMove);
                        app.updateMoves();
                    }
                    currentMoveX = 0;
                    currentMoveY = 0;
                    hasMoved = false;
                    currentMove = level.getLastMove();
                    moveStartTime += moveAnimationMs;
                    offsetTime = (int)(currentTime - moveStartTime);
                }

                if (currentMove == null) {
                    isUndoing = false;
                    animationTimer.stop();
                    if (moveAnimationMs > 0) {
                        moveEndTimer.start();
                    }
                } else {
                    pushedPack = currentMove.getPushedPack();
                    int offsetDistance = -(offsetTime * skinSize) / moveAnimationMs;
                    if (offsetDistance > skinSize/2) {
                        if (!hasMoved) {
                            level.undoMove(currentMove);
                            app.updateMoves();
                            hasMoved = true;
                        }
                        offsetDistance -= skinSize;
                    }

                    switch (currentMove.getDir()) {
                        case UP -> currentMoveY = -offsetDistance;
                        case DOWN -> currentMoveY = offsetDistance;
                        case LEFT -> currentMoveX = -offsetDistance;
                        case RIGHT -> currentMoveX = offsetDistance;
                    }
                }
            } else if (selectedPack != null) {
                long ms = System.currentTimeMillis();
                int diff = (int)(ms - lastPulseTime);
                if (diff >= pulseStepTime) {
                    lastPulseTime += pulseStepTime;
                    if (pulseIncreasing) {
                        currentPulseId++;
                        if (currentPulseId >= 8) {
                            pulseIncreasing = false;
                        }
                    } else {
                        currentPulseId--;
                        if (currentPulseId <= -2) {
                            pulseIncreasing = true;
                        }
                    }
                }
            } else {
                System.out.println("Stopping animation timer");
                animationTimer.stop();
            }

            repaint();
        }
    };

    private ActionListener moveEndAction = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            moveEndTimer.stop();
            currentMove = null;
            repaint();
        }
    };

    private MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!hasFocus()) {
                requestFocusInWindow();
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                int x = e.getX() - levelOffsetX;
                int y = e.getY() - levelOffsetY;

                if (x >= 0 && x < levelImage.getWidth() && y >= 0 && y < levelImage.getHeight()) {
                    int col = x / skinSize;
                    int row = y / skinSize;

                    System.out.format("Left click at (%d, %d)\n", col, row);
                    Pack pack = level.packAt(col, row);
                    if (pack != null) {
                        if (pack == selectedPack) {
                            selectedPack = null;
                            packMarkers = null;
                            System.out.println("Unselecting pack");
                        } else {
                            System.out.println("Getting pack reachable now");
                            ArrayList<Position> reachable = MoveHelper.getInstance().getPackReachable(pack, level);
                            if (!reachable.isEmpty()) {
                                manMarkers = null;
                                packMarkers = reachable;
                                selectedPack = pack;
                                currentPulseId = 0;
                                pulseIncreasing = true;
                                lastPulseTime = System.currentTimeMillis();
                                animationTimer.start();
                            }
                        }
                    } else if (selectedPack != null) {
                        System.out.println("Try moving pack now");
                        moves = MoveHelper.getInstance().movePack(selectedPack, level, col, row);
                        if (moves != null) {
                            packMarkers = null;
                            selectedPack = null;
                            animationTimer.stop();
                            animateMoves();
                        }
                    } else if (level.getMan().getCol() == col && level.getMan().getRow() == row) {
                        if (manMarkers != null) {
                            manMarkers = null;
                        } else {
                            manMarkers = MoveHelper.getInstance().getReachable(level);
                        }
                    } else {
                        System.out.println("Trying man move");
                        moves = MoveHelper.getInstance().moveMan(level, col, row);
                        if (moves != null) {
                            manMarkers = null;
                            animateMoves();
                        } else {
                            System.out.println("Moves was null");
                        }
                    }

                    repaint();
                }
            }
        }
    };

    private KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            Direction moveDir = Direction.NONE;

            switch (key) {
                case KeyEvent.VK_A:
                    moveDir = Direction.LEFT;
                    break;
                case KeyEvent.VK_D:
                    moveDir = Direction.RIGHT;
                    break;
                case KeyEvent.VK_W:
                    moveDir = Direction.UP;
                    break;
                case KeyEvent.VK_S:
                    moveDir = Direction.DOWN;
                    break;
            }

            if (!level.isComplete() && !animationTimer.isRunning()) {
                if (moveDir != Direction.NONE) {
                    Move m = level.getMove(moveDir);
                    if (m != null) {
                        manMarkers = null;
                        packMarkers = null;
                        selectedPack = null;
                        moves = new ArrayList<Move>();
                        moves.add(m);
                        animateMoves();
                    }
                } else if (key == KeyEvent.VK_BACK_SPACE) {
                    undoMove();
                }
            }
        }
    };
}
