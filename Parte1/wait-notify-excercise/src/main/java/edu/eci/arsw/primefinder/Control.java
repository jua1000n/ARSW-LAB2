/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class Control extends Thread {
    
    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;
    private List<Integer> objet = new LinkedList<>();

    private final int NDATA = MAXVALUE / NTHREADS;

    private PrimeFinderThread pft[];
    
    private Control() {
        super();
        this.pft = new  PrimeFinderThread[NTHREADS];

        int i;
        for(i = 0;i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i*NDATA, (i+1)*NDATA, objet);
            pft[i] = elem;
            System.out.println("Esta entrandoasdasdsadasdasd");
            try {
                controls();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pft[i] = new PrimeFinderThread(i*NDATA, MAXVALUE + 1, objet);

    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for(int i = 0;i < NTHREADS;i++ ) {
            pft[i].start();
        }
    }

    private void controls() throws InterruptedException {
        System.out.println("Esta entrando12");
        synchronized (objet) {
            System.out.println("Esta entrando");
            objet.notifyAll();
            /*while(true) {
                Scanner sc = new Scanner(System.in);
                String cadena = sc.nextLine();
                System.out.printf("Esta entrando");
                if (cadena == "\n") {
                    objet.notifyAll();
                }
            }*/
        }
    }
}