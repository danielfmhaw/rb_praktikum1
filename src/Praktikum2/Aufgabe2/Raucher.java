package Praktikum2.Aufgabe2;

public class Raucher extends Thread{

    private String name;
    private String benoetigteZutat1;
    private String benoetigteZutat2;
    private Tisch tisch;

    public Raucher(String name, String verfügbareZutat, Tisch tisch) {
        this.name=name;
        this.tisch = tisch;
        switch (verfügbareZutat) {
            case "Tabak":
                this.benoetigteZutat1 = "Papier";
                this.benoetigteZutat2 = "Streichholz";
                break;
            case "Papier":
                this.benoetigteZutat1 = "Tabak";
                this.benoetigteZutat2 = "Streichholz";
                break;
            case "Streichholz":
                this.benoetigteZutat1 = "Tabak";
                this.benoetigteZutat2 = "Papier";
                break;
            default:
                System.out.println("Unzulässige Zutat: " + verfügbareZutat);
                break;
        }
    }

    public void run() {
        synchronized (tisch){
            while (!Thread.currentThread().isInterrupted()) {
                while (!tisch.istVoll()){
                    try {
                        tisch.wait();
                    } catch (InterruptedException e) {

                    }
                }
                tisch.take(benoetigteZutat1, benoetigteZutat2,name);

                System.out.println(name + " kann mit dem Rauchen anfangen.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println(name+ " wurde beim Zigaretten rauchen unterbrochen (Sleep).");
                }

                System.out.println(name+ " hat das Rauchen beendet");
                System.out.println();
                System.out.println();
            }
        }
        System.out.println(name+ " ist interupted außer while() ");
    }
}