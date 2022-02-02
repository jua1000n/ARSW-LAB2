package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private JButton paus;
    private JButton resm;
    private JButton star;
    private JLabel largeSnak;
    private JLabel badSnake;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];
    private boolean estadobad = true;
    AtomicInteger longBody = new AtomicInteger(-1);
    AtomicInteger numSnake = new AtomicInteger(-1);
    AtomicInteger badSnakes = new AtomicInteger(-1);

    private List<Integer> estate = new LinkedList<Integer>();

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 50);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        
        
        frame.add(board,BorderLayout.CENTER);
        
        JPanel actionsBPabel=new JPanel();
        actionsBPabel.setLayout(new FlowLayout());

        paus = new JButton("Pause ");
        paus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pauseAction();
            }
        });

        resm = new JButton("Resume ");
        resm.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resumAction();
            }
        });

        star = new JButton("Start ");
        star.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startAction();
            }
        });

        actionsBPabel.add(new JButton("Action "));
        actionsBPabel.add(paus);
        actionsBPabel.add(resm);
        actionsBPabel.add(star);
        badSnake=new JLabel();
        largeSnak=new JLabel();
        largeSnak.setVisible(false);
        badSnake.setVisible(false);
        actionsBPabel.add(largeSnak);
        actionsBPabel.add(badSnake);
        frame.add(actionsBPabel,BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {

        
        for (int i = 0; i != MAX_THREADS; i++) {
            
            snakes[i] = new Snake(i + 1, spawn[i], i + 1, estate);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            //thread[i].start();
        }

        frame.setVisible(true);

            
        while (true) {
            //int x = 0;
            AtomicInteger x = new AtomicInteger(0);
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    //x++;
                    x.addAndGet(1);
                }
            }
            if (x.get() == MAX_THREADS) {
                break;
            }
            for (int i = 0; i != MAX_THREADS; i++) {
                if (thread[i].getState() == Thread.State.TERMINATED) {
                    if (estadobad) {
                        badSnakes = new AtomicInteger(i);
                        estadobad = false;
                    }
                }
            }
        }


        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }
        

    }

    private void resumAction() {
        largeSnak.setVisible(false);
        badSnake.setVisible(false);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 50);
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i].setEstatePause(false);
        }
        snakeApps();
    }

    private void pauseAction() {
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i].setEstatePause(true);
            int x = snakes[i].getSnakeBody().size();
            if(longBody.get() < x) {
                longBody = new AtomicInteger(x);
                numSnake = new AtomicInteger(i);
            }
        }
        infoSnake();

    }

    private void infoSnake() {
        largeSnak.setVisible(true);
        largeSnak.setText("The snake "+numSnake.get()+" is the more long with: "+longBody.get());
        badSnake.setVisible(true);
        badSnake.setText("The snake "+badSnakes.get()+" is the more bad");
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 300,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 50);
    }

    private void startAction() {
        for (int i = 0; i != MAX_THREADS; i++) {
            thread[i].start();
        }
        for (int i = 0; i != MAX_THREADS; i++) {
            if (thread[i].getState() == Thread.State.TERMINATED) {
                if (estadobad) {
                    badSnakes = new AtomicInteger(i);
                    estadobad = false;
                }
            }
        }
    }

    public void snakeApps() {
        synchronized (estate) {
            estate.notifyAll();
        }
    }

    public static SnakeApp getApp() {
        return app;
    }

}
