import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimRace {
    int  anzahl_Autos;
    int rundenanzahl;

    public SimRace(int rundenanzahl,int anzahl_Autos){
        this.rundenanzahl=rundenanzahl;
        this.anzahl_Autos=anzahl_Autos;
    }

    public static void main(String[] args) {
        SimRace test= new SimRace(10_000_000,10);
        //SimRace test= new SimRace(10,7);
        List <Car>autos= test.autosErstellen();
        test.autosAufBahn(autos);
    }

    public List<Car> autosErstellen(){
        List<Car> autos= new ArrayList<>();
        for (int i=0;i<anzahl_Autos;i++){
            String name= "Wagen " +i;
            Car car= new Car(name);
            car.start();
            autos.add(car);
        }
        return autos;
    }

    public void autosAufBahn(List<Car> autos){

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
        
        //Thread warten (alle im Ziel)
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
        int gesamtzeit=0;
        Collections.sort(autos);
        System.out.println("");
        System.out.println("****Endstand****");
        for(int i=0;i<autos.size();i++){
            gesamtzeit+=autos.get(i).gesamtzeit;
            System.out.println((i+1)+".Platz: "+autos.get(i).toString()+" Zeit:"+autos.get(i).gesamtzeit);
        }
        System.out.println(gesamtzeit);
    }
}
