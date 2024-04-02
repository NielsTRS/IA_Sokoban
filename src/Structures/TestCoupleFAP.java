package Structures;

public class TestCoupleFAP {
	public static void main(String[] args) {
		FAP<CoupleFAP> g = new FAPTableau<>();

		CoupleFAP couple1 = new CoupleFAP(1, 2, 5); // i=1, j=2, priorité=5
        CoupleFAP couple2 = new CoupleFAP(3, 4, 3); // i=3, j=4, priorité=3
        CoupleFAP couple3 = new CoupleFAP(5, 6, 7); // i=5, j=6, priorité=7

        g.insere(couple1);
        g.insere(couple2);
        g.insere(couple3);

        CoupleFAP couple = g.extrait();
		System.out.println("Extraction du couple" + couple);
    }
}