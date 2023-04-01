public class Car extends Thread implements Comparable {
    public int wartezeit;
    public int gesamtzeit;
    public String name;

    public Car(String name){
        this.name=name;
    }
    @Override
    public void run() {
        if(!isInterrupted()){
            //nothing
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
