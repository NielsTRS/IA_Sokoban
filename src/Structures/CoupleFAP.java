package Structures;

public class CoupleFAP implements Comparable<CoupleFAP> {
    public int i;
    public int j;
    public int priorite;

    public CoupleFAP(int i, int j, int priorite) {
        this.i = i;
        this.j = j;
        this.priorite = priorite;
    }

    @Override
    public String toString() {
        return "(" + i + ", " + j + ", Priorité: " + priorite + ")";
    }
    
    public int getPriorite() {
        return this.priorite;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    @Override
    public int compareTo(CoupleFAP autreCouple) {
        return Integer.compare(this.priorite, autreCouple.getPriorite());
    }
}
