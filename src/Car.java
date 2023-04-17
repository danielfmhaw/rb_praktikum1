public class Car extends Thread implements Comparable {
    public int wartezeit;
    public int gesamtzeit;
    public String name;

    private SimRace simRace;

    public Car(String name,SimRace simRace){
        this.name=name;
        this.simRace=simRace;
    }
    @Override
    public void run() {
        for(int i=0;i<simRace.rundenanzahl;i++){
            try {
                Thread.sleep(wartezeit);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setWartezeit((int)(Math.random() * ((100) + 1)));
        }
    }
    public void setWartezeit(int wartezeit){
        this.wartezeit=wartezeit;
        gesamtzeit+=wartezeit;
    }

    @Override
    public int compareTo(Object o) {
        Car anderer= (Car) o;
        if(this.gesamtzeit==anderer.gesamtzeit){
            return 0;
        }else if(this.gesamtzeit>anderer.gesamtzeit){
            return 1;
        }else{
            return -1;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
