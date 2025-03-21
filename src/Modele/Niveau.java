package Modele;
/*
 * Sokoban - Encore une nouvelle version (à but pédagogique) du célèbre jeu
 * Copyright (C) 2018 Guillaume Huard
 * 
 * Ce programme est libre, vous pouvez le redistribuer et/ou le
 * modifier selon les termes de la Licence Publique Générale GNU publiée par la
 * Free Software Foundation (version 2 ou bien toute autre version ultérieure
 * choisie par vous).
 * 
 * Ce programme est distribué car potentiellement utile, mais SANS
 * AUCUNE GARANTIE, ni explicite ni implicite, y compris les garanties de
 * commercialisation ou d'adaptation dans un but spécifique. Reportez-vous à la
 * Licence Publique Générale GNU pour plus de détails.
 * 
 * Vous devez avoir reçu une copie de la Licence Publique Générale
 * GNU en même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307,
 * États-Unis.
 * 
 * Contact:
 *          Guillaume.Huard@imag.fr
 *          Laboratoire LIG
 *          700 avenue centrale
 *          Domaine universitaire
 *          38401 Saint Martin d'Hères
 */

import Global.Configuration;
import Structures.Iterateur;

import Structures.CoupleFAP;
import java.util.List;
import java.util.ArrayList;

public class Niveau extends Historique<Coup> implements Cloneable {
	static final int VIDE = 0;
	static final int MUR = 1;
	static final int POUSSEUR = 2;
	static final int CAISSE = 4;
	static final int BUT = 8;
	int l, c;
	int[][] cases;
	String nom;
	int pousseurL, pousseurC;
	int nbButs;
	int nbCaissesSurBut;
	int nbPas, nbPoussees;

	Niveau() {
		cases = new int[1][1];
		l = c = 1;
		pousseurL = pousseurC = -1;
	}

	int ajuste(int cap, int objectif) {
		while (cap <= objectif) {
			cap = cap * 2;
		}
		return cap;
	}

	void redimensionne(int nouvL, int nouvC) {
		int capL = ajuste(cases.length, nouvL);
		int capC = ajuste(cases[0].length, nouvC);
		if ((capL > cases.length) || (capC > cases[0].length)) {
			int[][] nouvelles = new int[capL][capC];
			for (int i = 0; i < cases.length; i++)
				for (int j = 0; j < cases[0].length; j++)
					nouvelles[i][j] = cases[i][j];
			cases = nouvelles;
		}
		if (nouvL >= l)
			l = nouvL + 1;
		if (nouvC >= c)
			c = nouvC + 1;
	}

	void fixeNom(String s) {
		nom = s;
	}

	void videCase(int i, int j) {
		redimensionne(i, j);
		cases[i][j] = VIDE;
	}

	void supprime(int contenu, int i, int j) {
		if (aBut(i, j)) {
			if (aCaisse(i, j) && ((contenu & CAISSE | contenu & BUT) != 0))
				nbCaissesSurBut--;
			if ((contenu & BUT) != 0)
				nbButs--;
		}
		if (aPousseur(i, j) && ((contenu & POUSSEUR) != 0))
			pousseurL = pousseurC = -1;
		cases[i][j] &= ~contenu;
	}

	void ajoute(int contenu, int i, int j) {
		redimensionne(i, j);
		int resultat = cases[i][j] | contenu;
		if ((resultat & BUT) != 0) {
			if (((resultat & CAISSE) != 0) && (!aCaisse(i, j) || !aBut(i, j)))
				nbCaissesSurBut++;
			if (!aBut(i, j))
				nbButs++;
		}
		if (((resultat & POUSSEUR) != 0) && !aPousseur(i, j)) {
			if (pousseurL != -1)
				throw new IllegalStateException("Plusieurs pousseurs sur le terrain !");
			pousseurL = i;
			pousseurC = j;
		}
		cases[i][j] = resultat;
	}

	int contenu(int i, int j) {
		return cases[i][j] & (POUSSEUR | CAISSE);
	}

	int decompteMouvement(Mouvement m) {
		if (m != null)
			return m.decompte();
		else
			return 0;
	}

	void decomptes(Coup cp) {
		nbPas += decompteMouvement(cp.pousseur());
		nbPoussees += decompteMouvement(cp.caisse());
	}

	@Override
	public void faire(Coup cp) {
		cp.fixeNiveau(this);
		decomptes(cp);
		super.faire(cp);
	}

	@Override
	public Coup annuler() {
		Coup cp = super.annuler();
		decomptes(cp);
		return cp;
	}

	@Override
	public Coup refaire() {
		Coup cp = super.refaire();
		decomptes(cp);
		return cp;
	}

	public Coup elaboreCoup(int dLig, int dCol) {
		int destL = pousseurL + dLig;
		int destC = pousseurC + dCol;
		Coup resultat = new Coup();

		if (aCaisse(destL, destC)) {
			int dCaisL = destL + dLig;
			int dCaisC = destC + dCol;

			if (estOccupable(dCaisL, dCaisC)) {
				resultat.deplacementCaisse(destL, destC, dCaisL, dCaisC);
			} else {
				return null;
			}
		}
		if (!aMur(destL, destC)) {
			resultat.deplacementPousseur(pousseurL, pousseurC, destL, destC);
			return resultat;
		}
		return null;
	}

	Coup deplace(int i, int j) {
		Coup cp = elaboreCoup(i, j);
		if (cp != null)
			faire(cp);
		return cp;
	}

	void ajouteMur(int i, int j) {
		ajoute(MUR, i, j);
	}

	void ajoutePousseur(int i, int j) {
		ajoute(POUSSEUR, i, j);
	}

	void ajouteCaisse(int i, int j) {
		ajoute(CAISSE, i, j);
	}

	void ajouteBut(int i, int j) {
		ajoute(BUT, i, j);
	}

	public int lignes() {
		return l;
	}

	public int colonnes() {
		return c;
	}

	String nom() {
		return nom;
	}

	boolean estVide(int l, int c) {
		return cases[l][c] == VIDE;
	}

	public boolean aMur(int l, int c) {
		return (cases[l][c] & MUR) != 0;
	}

	public boolean aBut(int l, int c) {
		return (cases[l][c] & BUT) != 0;
	}

	public boolean aPousseur(int l, int c) {
		return (cases[l][c] & POUSSEUR) != 0;
	}

	public boolean aCaisse(int l, int c) {
		return (cases[l][c] & CAISSE) != 0;
	}

	public boolean estOccupable(int l, int c) {
		return (cases[l][c] & (MUR | CAISSE)) == 0;
	}

	public boolean estTermine() {
		return nbCaissesSurBut == nbButs;
	}

	public int lignePousseur() {
		return pousseurL;
	}

	public int colonnePousseur() {
		return pousseurC;
	}

	// Par convention, la méthode clone de java requiert :
	// - que la classe clonée implémente Cloneable
	// - que le resultat soit construit avec la méthode clone de la classe parente (pour qu'un clonage
	//   profond fonctionne sur toute l'ascendence de l'objet)
	// Le nouvel objet sera de la même classe que l'objet cible du clonage (creation spéciale dans Object)
	@Override
	public Niveau clone() {
		try {
			Niveau resultat = (Niveau) super.clone();
			// Le clone de base est un clonage à plat pour le reste il faut
			// cloner à la main : cela concerne les cases
			resultat.cases = new int[cases.length][];
			for (int i=0; i< cases.length; i++)
				resultat.cases[i] = cases[i].clone();
			return resultat;
		} catch (CloneNotSupportedException e) {
			Configuration.erreur("Bug interne, niveau non clonable");
		}
		return null;
	}

	public int marque(int i, int j) {
		return (cases[i][j] >> 8) & 0xFFFFFF;
	}

	public void fixerMarque(int m, int i, int j) {
		cases[i][j] = (cases[i][j] & 0xFF) | (m << 8);
	}

	public int nbPas() {
		return nbPas;
	}

	public int nbPoussees() {
		return nbPoussees;
	}

	public List<CoupleFAP> voisins(int u, int v) {
        List<CoupleFAP> voisins = new ArrayList<>();

		if (u >= 0 && u < lignes() && v+1 >= 0 && v+1 < colonnes())
        	voisins.add(new CoupleFAP(u, v+1, 1));
		if (u+1 >= 0 && u+1 < lignes() && v+1 >= 0 && v+1 < colonnes())	
        	voisins.add(new CoupleFAP(u+1, v, 1));
		if (u >= 0 && u < lignes() && v-1 >= 0 && v-1 < colonnes())
			voisins.add(new CoupleFAP(u, v-1, 1));
		if (u-1 >= 0 && u-1 < lignes() && v >= 0 && v < colonnes())
        	voisins.add(new CoupleFAP(u-1, v, 1));
			
        return voisins;
    }
}
