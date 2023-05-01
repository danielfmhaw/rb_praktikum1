package Praktikum2.Aufgabe2;

public class Tisch {
    private String zutat1;
    private String zutat2;

    public synchronized void put(String zutat1, String zutat2,String name) {
        while (!Thread.currentThread().isInterrupted() && this.zutat1 != null || this.zutat2 != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        this.zutat1 = zutat1;
        this.zutat2 = zutat2;
        if(istVoll()){
            System.out.println(name + " hat " + zutat1 + " und " + zutat2 + " auf den Tisch gelegt.");
        }

        notifyAll();
    }

    public synchronized void take(String zutat1, String zutat2,String name) {
        while (!Thread.currentThread().isInterrupted() && this.zutat1 == null || this.zutat2 == null ||
                (!this.zutat1.equals(zutat1) && !this.zutat1.equals(zutat2)) ||
                (!this.zutat2.equals(zutat1) && !this.zutat2.equals(zutat2))) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        this.zutat1 = null;
        this.zutat2 = null;
        System.out.println(name+ " hat " + zutat1 + " und " + zutat2 + " vom Tisch genommen.");

        notifyAll();
    }

    public boolean istVoll(){
        return zutat1!=null && zutat2!=null;
    }
}
