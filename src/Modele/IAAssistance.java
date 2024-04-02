package Modele;

import Global.Configuration;
import Structures.Sequence;
import Structures.Astar;
import Structures.Case;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

class IAAssistance extends IA {

    @Override
    public Sequence<Coup> joue() {
        Sequence<Coup> resultat = Configuration.nouvelleSequence();
        int pousseurL = niveau.lignePousseur();
        int pousseurC = niveau.colonnePousseur();
        int trouve_caisse = 0;
        int trouve_but = 0;
        Case caisse = null;
        Case but = null;

        Configuration.info("Entrée dans la méthode de jeu de l'IA");

        // Trouver les coordonnées de la caisse et du but
        for (int i = 0; i < niveau.lignes(); i++) {
            for (int j = 0; j < niveau.colonnes(); j++) {
                if (niveau.aCaisse(i, j)) {
                    caisse = new Case(i, j);
                    trouve_caisse = 1;
                } else if (niveau.aBut(i, j)) {
                    but = new Case(i, j);
                    trouve_but = 1;
                }
                if (trouve_caisse == 1 && trouve_but == 1)
                    break;
            }
        }

        // Trouver la case à atteindre par le pousseur
        Case caseOptimale = Trouver_case_pousseur(niveau, caisse, but);

        if (caseOptimale == null) {
			System.err.println("Pas de solution possible, au suivant.");
            /* System.exit(1); */
			return null;
        }

		Astar astar_pousseurCase = new Astar();
        Astar.AstarResult astar_pousseur = astar_pousseurCase.astar(niveau, pousseurL, pousseurC, caseOptimale.x, caseOptimale.y);

		pousseurL = caseOptimale.x;
        pousseurC = caseOptimale.y;

        // Déplacer le pousseur jusqu'à la case optimale à côté de la caisse
        Case[][] pred_pousseur = astar_pousseur.pred;
        Sequence<Case> seq = Configuration.nouvelleSequence();
        while (pred_pousseur[caseOptimale.x][caseOptimale.y] != null) {
            Case nouveau = pred_pousseur[caseOptimale.x][caseOptimale.y];
            Case dir = new Case(caseOptimale.x - nouveau.x, caseOptimale.y - nouveau.y);
            seq.insereTete(dir);
            caseOptimale.x = nouveau.x;
            caseOptimale.y = nouveau.y;
        }

        while (!seq.estVide()) {
            Coup coup = new Coup();
            Case nouv = seq.extraitTete();
            coup = niveau.deplace(nouv.x, nouv.y);
            resultat.insereQueue(coup);
        }

        Coup coup = niveau.deplace(caisse.x - pousseurL, caisse.y - pousseurC);
        resultat.insereQueue(coup);

        Configuration.info("Sortie de la méthode de jeu de l'IA");
        return resultat;
    }

    public List<Case> cases_libres_autour_caisse(Case caisse) {
        List<Case> cases = new ArrayList<>();
        if (niveau.estOccupable(caisse.ouest().x, caisse.ouest().y)) {
            Case caseOuest = new Case(caisse.ouest().x, caisse.ouest().y);
            cases.add(caseOuest);
        }
        if (niveau.estOccupable(caisse.est().x, caisse.est().y)) {
            Case caseEst = new Case(caisse.est().x, caisse.est().y);
            cases.add(caseEst);
        }
        if (niveau.estOccupable(caisse.sud().x, caisse.sud().y)) {
            Case caseSud = new Case(caisse.sud().x, caisse.sud().y);
            cases.add(caseSud);
        }
        if (niveau.estOccupable(caisse.nord().x, caisse.nord().y)) {
            Case caseNord = new Case(caisse.nord().x, caisse.nord().y);
            cases.add(caseNord);
        }
        return cases;
    }

    public boolean estAccessiblePousseur(Niveau niveau, Case casePousseur, int pousseurL, int pousseurC) {
        Astar astar_pousseurCase = new Astar();
        Astar.AstarResult astar_pousseur = astar_pousseurCase.astar(niveau, pousseurL, pousseurC, casePousseur.x, casePousseur.y);
        int distance = astar_pousseur.distance;
        return distance != Integer.MAX_VALUE;
    }

	public Case caseOpposee(Case casePousseur, Case caisse) {
		if (casePousseur.x == caisse.est().x && casePousseur.y == caisse.est().y) {
			return new Case(caisse.ouest().x, caisse.ouest().y);
		} else if (casePousseur.x == caisse.ouest().x && casePousseur.y == caisse.ouest().y) {
			return new Case(caisse.est().x, caisse.est().y);
		} else if (casePousseur.x == caisse.sud().x && casePousseur.y == caisse.sud().y) {
			return new Case(caisse.nord().x, caisse.nord().y);
		} else { //case = au nord de la caisse
			return new Case(caisse.sud().x, caisse.sud().y);
		}
	}

    public boolean est_case_opposee_libre(Case casePousseur, Case caisse) {
    	Case caseOpposee = caseOpposee(casePousseur, caisse);
    	return niveau.estOccupable(caseOpposee.x, caseOpposee.y);
	}

    public boolean ne_bloque_pas(Case casePousseur, Case caisse) {
        if (casePousseur.x == caisse.est().x && casePousseur.y == caisse.est().y) {
            return !(niveau.aMur(caisse.ouest().ouest().x, caisse.ouest().ouest().y) &&
                    (niveau.aMur(caisse.ouest().nord().x, caisse.ouest().nord().y) ||
                            niveau.aMur(caisse.ouest().sud().x, caisse.ouest().sud().y)));
        } else if (casePousseur.x == caisse.ouest().x && casePousseur.y == caisse.ouest().y) {
            return !(niveau.aMur(caisse.est().est().x, caisse.est().est().y) &&
                    (niveau.aMur(caisse.est().nord().x, caisse.est().nord().y) ||
                            niveau.aMur(caisse.est().sud().x, caisse.est().sud().y)));
        } else if (casePousseur.x == caisse.sud().x && casePousseur.y == caisse.sud().y) {
            return !(niveau.aMur(caisse.nord().nord().x, caisse.nord().nord().y) &&
                    (niveau.aMur(caisse.nord().ouest().x, caisse.nord().ouest().y) ||
                            niveau.aMur(caisse.nord().est().x, caisse.nord().est().y)));
        } else { //case = au nord de la caisse
            return !(niveau.aMur(caisse.sud().sud().x, caisse.sud().sud().y) &&
                    (niveau.aMur(caisse.sud().ouest().x, caisse.sud().ouest().y) ||
                            niveau.aMur(caisse.sud().est().x, caisse.sud().est().y)));
        }
    }

    public Case Trouver_case_pousseur(Niveau niveau, Case caisse, Case but) {
        int min = Integer.MAX_VALUE;
        Case caseOptimale = null;
		int pousseurL = niveau.lignePousseur();
        int pousseurC = niveau.colonnePousseur();
        List<Case> casesLibres = new ArrayList<>();
        List<Case> casesAccessibles = new ArrayList<>();
        List<Case> casesPreSelection = new ArrayList<>();
        List<Case> casesChoixPossibles = new ArrayList<>();

        casesLibres = cases_libres_autour_caisse(caisse);

        for (Case casePousseur : casesLibres) {
            if (estAccessiblePousseur(niveau, casePousseur, pousseurL, pousseurC)) {
                casesAccessibles.add(casePousseur);
            }
        }

        for (Case casePousseur : casesAccessibles) {
            if (est_case_opposee_libre(casePousseur, caisse)) {
                casesPreSelection.add(casePousseur);
            }
        }

        for (Case casePousseur : casesPreSelection) {
            if (ne_bloque_pas(casePousseur, caisse)) {
                casesChoixPossibles.add(casePousseur);
            }
        }

        for (Case casePousseur : casesChoixPossibles) {
            Astar astar_caseCaisse = new Astar();
            Astar.AstarResult astar_case = astar_caseCaisse.astar(niveau, caseOpposee(casePousseur,caisse).x, caseOpposee(casePousseur,caisse).y, but.x, but.y);
            int distance = astar_case.distance;

            if (min >= distance) {
				min = distance;
                caseOptimale = casePousseur;
            }
        }

        if (caseOptimale == null) {
            return null;
        }

        return caseOptimale;
    }
}
