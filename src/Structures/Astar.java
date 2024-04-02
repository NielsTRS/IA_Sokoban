package Structures;

import Modele.Niveau;
import java.util.Arrays;

public class Astar {

    public class AstarResult {
        public int distance;
        public Case[][] pred;

        public AstarResult(int distance, Case[][] pred) {
            this.distance = distance;
            this.pred = pred;
        }
    }

    public int Heuristique(int u, int v, int a, int b) {
        return Math.abs(u-a) + Math.abs(v-b);
    }

    public AstarResult astar(Niveau niveau, int x, int y, int a, int b) {
        int lignes = niveau.lignes();
        int colonnes = niveau.colonnes();
        int[][] dist = new int[lignes][colonnes];
        Case[][] pred = new Case[lignes][colonnes];

        for (int i = 0; i < dist.length; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
        }
        dist[x][y] = 0;

        FAPTableau<CoupleFAP> filePriorite = new FAPTableau<>();
        CoupleFAP couple = new CoupleFAP(x,y,0);
        filePriorite.insere(couple);

        while (!filePriorite.estVide()) {
            couple = filePriorite.extrait();
            x = couple.i;
            y = couple.j;

            if (x == a && y == b) {
                return new AstarResult(dist[a][b], pred);
            }

            for (CoupleFAP voisin : niveau.voisins(x, y)) {
                int u = voisin.i;
                int v = voisin.j;
                if (niveau.estOccupable(u, v)) {
                    int poidsVoisin = dist[x][y] + 1;
                    if (poidsVoisin < dist[u][v]) {
                        dist[u][v] = poidsVoisin;
                        pred[u][v] = new Case(x, y);
                        couple = new CoupleFAP(u,v,poidsVoisin + Heuristique(u,v,a,b));
                        filePriorite.insere(couple);
                    } 
                }
            }   
        }
        return new AstarResult(dist[a][b], pred);
    }
}
