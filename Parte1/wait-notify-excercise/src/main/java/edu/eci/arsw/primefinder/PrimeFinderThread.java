package edu.eci.arsw.primefinder;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static com.sun.java.accessibility.util.AWTEventMonitor.addKeyListener;

public class PrimeFinderThread extends Thread {

	
	int a,b;
    private int timeStop = 100;
    private long time;
	
	private List<Integer> primes;
	List<Integer> objet;
	
	public PrimeFinderThread(int a, int b, List<Integer> objet) {
		super();
		this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
		this.objet= objet;
		time = System.currentTimeMillis();
	}

        @Override
	public void run(){
	    for (int i= a;i < b;i++){
	        if (isPrime(i)){
	            primes.add(i);
	            try {
	                if ((System.currentTimeMillis() - time) >= timeStop) {
                        primeFinder();
                    }
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            System.out.println(i);
	        }
	    }
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

    private void primeFinder() throws InterruptedException {
        synchronized (objet) {
			System.out.println("numero de primos"+ primes.size());
			objet.wait();
        }
    }

	public List<Integer> getPrimes() {
		return primes;
	}
}
