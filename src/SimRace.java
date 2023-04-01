import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimRace {
    int anzahl_Autos;
    int rundenanzahl;

    public SimRace(int rundenanzahl){
        this.rundenanzahl=rundenanzahl;
    }

    public static void main(String[] args) {
        Car auto1= new Car("Wagen 0");
        Car auto2= new Car("Wagen 1");
        Car auto3= new Car("Wagen 2");
        Car auto4= new Car("Wagen 3");
        Car auto5= new Car("Wagen 4");
        ArrayList <Car>autos= new ArrayList<>( Arrays.asList(auto1,auto2,auto3,auto4,auto5));
        SimRace test= new SimRace(11);
        //SimRace test= new SimRace(10_000_000);
        test.autosAufBahn(autos);
    }

    /**
        Aufgabe 1- Methode
     */
    public void autosAufBahn(List<Car> autos){
        //Thread starten
        for(int i=0;i<autos.size();i++){
            autos.get(i).start();
        }
        long startTime = System.nanoTime();

        //Anzahl an Runden entsprechend oft neue Rundenzeit berechnen und warten
        for(int j=0;j<rundenanzahl;j++){
            for(int i=0;i<autos.size();i++){
                //System.out.println(autos.get(i).getName());
                try {
                    autos.get(i).sleep(autos.get(i).wartezeit);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                autos.get(i).setWartezeit((int)(Math.random() * ((100) + 1)));
            }
            //System.out.println("Runde "+j+" beendet");
        }
        
        //Thread warten
        try {
            for(int i=0;i<autos.size();i++){
                autos.get(i).join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long usedTime = (System.nanoTime() - startTime)/1000000;
        System.out.println("Gesamtzeit beträgt: "+usedTime);
        konsolenAusgabe(autos);
    }

    private void konsolenAusgabe(List<Car> autos){
        Collections.sort(autos);
        System.out.println("");
        System.out.println("****Endstand****");
        for(int i=0;i<autos.size();i++){
            System.out.println((i+1)+".Platz: "+autos.get(i).toString()+" Zeit:"+autos.get(i).gesamtzeit);
        }
    }
}
