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
        List<Car> autos= new ArrayList<>();
        Car auto1= new Car("Wagen 0");
        Car auto2= new Car("Wagen 1");
        Car auto3= new Car("Wagen 2");
        Car auto4= new Car("Wagen 3");
        Car auto5= new Car("Wagen 4");
        autos.add(auto1);
        autos.add(auto2);
        autos.add(auto3);
        autos.add(auto4);
        autos.add(auto5);
        SimRace test= new SimRace(10_000_000);
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
        //Anzahl an Runden entsprechend oft neue Rundenzeit berechnen und warten
        for(int j=0;j<rundenanzahl;j++){
            for(int i=0;i<autos.size();i++){
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
        for(int i=0;i<autos.size();i++){
            try {
                autos.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        konsolenAusgabe(autos);
    }

    private void konsolenAusgabe(List<Car> autos){
        Collections.sort(autos);

        System.out.println("****Endstand****");
        for(int i=0;i<autos.size();i++){
            System.out.println((i+1)+".Platz: "+autos.get(i).toString()+" Zeit:"+autos.get(i).gesamtzeit);
        }
    }
}
