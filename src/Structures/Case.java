package Structures;

public class Case {
    public int x;
    public int y;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Case nord() {
        return new Case(x-1,y);
    }
    public Case sud() {
        return new Case(x+1,y);
    }
    public Case est() {
        return new Case(x,y+1);
    }
    public Case ouest() {
        return new Case(x,y-1);
    }

    @Override
    public String toString(){
        return "Caisse : " + x + ", " + y;
    }
}