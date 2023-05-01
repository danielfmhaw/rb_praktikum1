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
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (tisch){
                while (!tisch.istVoll()){
                    try {
                        tisch.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                tisch.take(benoetigteZutat1, benoetigteZutat2,name);

                System.out.println(name + " kann mit dem Rauchen anfangen.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(name+ " wurde beim Zigaretten rauchen unterbrochen.");
                }

                System.out.println(name+ " hat das Rauchen beendet");
                System.out.println();
                System.out.println();

                tisch.put(null, null,name);
                tisch.notify();
            }
        }
    }
}