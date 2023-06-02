package Praktikum2.Aufgabe2;

import java.util.Random;

public class Agent extends Thread{
    private String name;
    private Tisch tisch;
    private String[] zutaten = {"Tabak", "Papier", "Streichholz"};
    private Random random = new Random();

    public Agent(Tisch tisch,String name) {
        this.tisch = tisch;
        this.name=name;
    }

    public void run() {
        synchronized (tisch) {
            while (!Thread.currentThread().isInterrupted()) {
                while (tisch.istVoll()){
                    try {
                        tisch.wait();
                    } catch (InterruptedException e) {
                    }
                }
                int zahl1 = (int) (3 * Math.random());
                int zahl2 = (int) (3 * Math.random());

                while (zahl1 == zahl2) {
                    zahl2 = random.nextInt(3);
                }

                String zutat1 = zutaten[zahl1];
                String zutat2 = zutaten[zahl2];


                tisch.put(zutat1, zutat2, name);
            }
            System.out.println(name+ " ist interupted");
        }
    }
}
