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

import Controleur.ControleurMediateur;
import Global.Configuration;
import Modele.Jeu;
import Modele.LecteurNiveaux;
import Vue.CollecteurEvenements;
import Vue.InterfaceGraphique;
import Vue.InterfaceTextuelle;

import java.io.InputStream;

public class Sokoban {
	final static String typeInterface = Configuration.typeInterface;

	public static void main(String[] args) {
		InputStream in;
		if (args.length > 0)
			in = Configuration.ouvre("Niveaux/" + args[0] + ".txt");
		else
			in = Configuration.ouvre("Niveaux/Tests.txt");
		Configuration.info("Niveaux trouvés");

		LecteurNiveaux l = new LecteurNiveaux(in);
		Jeu j = new Jeu(l);
		CollecteurEvenements control = new ControleurMediateur(j);
		switch (typeInterface) {
			case "Graphique":
				InterfaceGraphique.demarrer(j, control);
				break;
			case "Textuelle":
				InterfaceTextuelle.demarrer(j, control);
				break;
			default:
				Configuration.erreur("Interface inconnue");
		}
	}
}
