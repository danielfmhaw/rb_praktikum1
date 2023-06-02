package Praktikum2.Aufgabe2;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

    List<Raucher> raucher= new ArrayList<>();

    public void startSimulation() throws InterruptedException {
        Tisch tisch= new Tisch();

        Raucher raucher1= new Raucher("Raucher 1","Tabak",tisch);
        Raucher raucher2= new Raucher("Raucher 2","Papier",tisch);
        Raucher raucher3= new Raucher("Raucher 3","Streichholz",tisch);


        Agent agent1= new Agent(tisch,"Agent Nr1");
        Agent agent2= new Agent(tisch,"Agent Nr2");


        // Threads starten
        agent1.start();
        agent2.start();
        raucher1.start();
        raucher2.start();
        raucher3.start();

        // Programm für 10 Sekunden laufen lassen
        Thread.sleep(1000);


        System.out.println("");
        System.out.println("");
        System.out.println("-------System gleich unterbrochen!!!-------");


        // Alle Threads beenden
        agent1.interrupt();
        agent2.interrupt();
        raucher1.interrupt();
        raucher2.interrupt();
        raucher3.interrupt();

    }

    public static void main(String args[])  {
        try {
            new Simulation().startSimulation();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
