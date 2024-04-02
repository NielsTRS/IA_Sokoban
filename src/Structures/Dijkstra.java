package Structures;

import Modele.Niveau;
import java.util.Arrays;

public class Dijkstra {

    public class DijkstraResult {
        public int[][] dist;
        public Case[][] pred;

        public DijkstraResult(int[][] dist, Case[][] pred) {
            this.dist = dist;
            this.pred = pred;
        }
    }

    public DijkstraResult dijkstra(Niveau niveau, int x, int y) {
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

            for (CoupleFAP voisin : niveau.voisins(x, y)) {
                int u = voisin.i;
                int v = voisin.j;
                if (niveau.estOccupable(u, v)) {
                    int poidsVoisin = dist[x][y] + 1;
                    if (poidsVoisin < dist[u][v]) {
                        dist[u][v] = poidsVoisin;
                        pred[u][v] = new Case(x, y);
                        couple = new CoupleFAP(u,v,poidsVoisin);
                        filePriorite.insere(couple);
                    }   
                }
            }   
        }
        return new DijkstraResult(dist, pred);
    }
}
