package com.inatsuz.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

public class Snake implements ActionListener, KeyListener {

    public static Snake snake;
    private Timer timer;
    private Random random;
    private Renderer renderer;
    private JFrame jFrame;
    private Image snakeHead = new ImageIcon(this.getClass().getResource("/resources/snakeHead.png")).getImage();
    private Image snakeTail = new ImageIcon(this.getClass().getResource("/resources/snakeTail.png")).getImage();

    private final int WIDTH = 700, HEIGHT = 700; // JFrame Size
    private int snakeLength, gameState = 0; // Snake length Default = 5, Game State: 0 ---> Not running, 1 ---> Running, 2 ---> Over 
    private int appleX, appleY;

    private int[] x = new int[WIDTH / 20 * HEIGHT / 20 + 10];
    private int[] y = new int[HEIGHT / 20 * WIDTH / 20 + 10];

    private boolean up = false, down = false, left = false, right = false;
    private boolean snakeMoving = false;
    private boolean canChange = true;
    private boolean apple = false;
    private boolean invalidLocation = false;

    public Snake() {
        timer = new Timer(40, this);
        random = new Random();
        renderer = new Renderer();

        jFrame = new JFrame("Snake");
        jFrame.setVisible(true);
        jFrame.setSize(706, 726);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setResizable(false);
        jFrame.add(renderer);
        jFrame.addKeyListener(this);

        timer.start();
    }

    public static void main(String[] args) {
        snake = new Snake();
    }

    private void start() {
        snakeLength = 5;
        apple = false;
        left = false;
        right = false;
        up = false;
        down = false;
        gameState = 1;

        for (int i = 0; i < snakeLength; i++) {
            x[i] = (int) (Math.floor(WIDTH / 20 / 2) * 20 + (i * 20));
            y[i] = (int) Math.floor(HEIGHT / 20 / 2) * 20;
        }
    }

    private void update() {
        move();
        checkAppleCollision();
        if (!apple) {
            spawnApple();
        }
        checkSnakeCollision();
    }

    private void move() {
        if (snakeMoving) {
            if (right) {
                if (x[0] + 20 > WIDTH - 20) {
                    gameState = 2;
                    snakeMoving = false;
                }
                for (int i = snakeLength; i > 0 && x[0] + 20 <= WIDTH - 20; i--) {
                    x[i] = x[i - 1];
                    y[i] = y[i - 1];
                }
                if (x[0] + 20 <= WIDTH - 20) {
                    x[0] += 20;
                }
            } else if (left) {
                if (x[0] - 20 < 0) {
                    gameState = 2;
                    snakeMoving = false;
                }
                for (int i = snakeLength; i > 0 && x[0] - 20 >= 0; i--) {
                    x[i] = x[i - 1];
                    y[i] = y[i - 1];
                }
                if (x[0] - 20 >= 0) {
                    x[0] -= 20;
                }
            } else if (up) {
                if (y[0] - 20 < 0) {
                    gameState = 2;
                    snakeMoving = false;
                }
                for (int i = snakeLength; i > 0 && y[0] - 20 >= 0; i--) {
                    x[i] = x[i - 1];
                    y[i] = y[i - 1];
                }
                if (y[0] - 20 >= 0) {
                    y[0] -= 20;
                }
            } else if (down) {
                if (y[0] + 20 > HEIGHT - 15) {
                    gameState = 2;
                    snakeMoving = false;
                }
                for (int i = snakeLength; i > 0 && y[0] + 20 <= HEIGHT - 15; i--) {
                    x[i] = x[i - 1];
                    y[i] = y[i - 1];
                }
                if (y[0] + 20 <= HEIGHT - 15) {
                    y[0] += 20;
                }
            }
            canChange = true;
        }
    }

    private void spawnApple() {
        do {
            invalidLocation = false;
            appleX = random.nextInt(34) * 20;
            appleY = random.nextInt(34) * 20;
            for (int i = snakeLength - 1; i >= 0; i--) {
                if (x[i] >= appleX && x[i] <= appleX + 19 && y[i] >= appleY && y[i] <= appleY + 19) {
                    invalidLocation = true;
                }
            }
            if (!invalidLocation) {
                apple = true;
            }
        } while (!apple);
    }

    private void checkAppleCollision() {
        if (x[0] >= appleX && x[0] <= appleX + 19 && y[0] >= appleY && y[0] <= appleY + 19) {
            snakeLength++;
            apple = false;
        }
    }

    private void checkSnakeCollision() {
        for (int i = snakeLength; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                gameState = 2;
                snakeMoving = false;
            }
        }
    }

    public void render(Graphics2D g) {
        g.setColor(Color.DARK_GRAY.darker().darker());
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (gameState == 0) {
            g.setFont(new Font("Arial", 1, 50));
            g.setColor(Color.WHITE);
            g.drawString("Press Space to Play", WIDTH / 2 - 240, HEIGHT / 2);
        } else if (gameState == 1) {
            g.setColor(Color.GREEN);
            g.fillOval(appleX, appleY, 20, 20);
            for (int i = snakeLength - 1; i >= 0; i--) {
                if (i == 0) {
                    g.drawImage(snakeHead, x[i], y[i], null);
                } else {
                    g.drawImage(snakeTail, x[i], y[i], null);
                }
            }
        } else if (gameState == 2) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", 1, 50));
            g.drawString("GAME OVER", WIDTH / 2 - 150, HEIGHT / 2 - 50);
            g.drawString("SCORE:" + (snakeLength - 5), WIDTH / 2 - 120, HEIGHT / 2 + 50);
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (gameState == 1) {
            update();
        }
        renderer.repaint();
    }

    public void keyTyped(KeyEvent event) {

    }

    public void keyPressed(KeyEvent event) {
        int id = event.getKeyCode();
        if (id == KeyEvent.VK_SPACE) {
            if (gameState == 0) {
                start();
            } else if (gameState == 1) {

            } else if (gameState == 2) {
                gameState = 0;
            }
        }
        if ((id == KeyEvent.VK_LEFT || id == KeyEvent.VK_UP || id == KeyEvent.VK_DOWN) && gameState == 1 && snakeMoving == false) {
            snakeMoving = true;
            canChange = true;
            if (id == KeyEvent.VK_LEFT) {
                left = true;
            } else if (id == KeyEvent.VK_UP) {
                up = true;
            } else if (id == KeyEvent.VK_DOWN) {
                down = true;
            }
        }
        if (snakeMoving && canChange) {
            if (id == KeyEvent.VK_LEFT && gameState == 1 && x[0] - 20 >= 0 && right == false && left == false) {
                up = false;
                down = false;
                left = true;
            } else if (id == KeyEvent.VK_RIGHT && gameState == 1 && x[0] + 20 <= WIDTH - 20 && left == false && right == false) {
                up = false;
                down = false;
                right = true;
            } else if (id == KeyEvent.VK_UP && gameState == 1 && y[0] - 20 >= 0 && down == false && up == false) {
                right = false;
                left = false;
                up = true;
            } else if (id == KeyEvent.VK_DOWN && gameState == 1 && y[0] + 20 <= HEIGHT - 15 && up == false && down == false) {
                right = false;
                left = false;
                down = true;
            }
            canChange = false;
        }
    }

    public void keyReleased(KeyEvent event) {

    }

}
