# OMATL : Outil de calcul formel

## Dependances

* **`ocaml >= 4.05`**
* **`Lablgtk3`**
* **`dune 2.0`**
* **`camlp5`**
* ...

## Calcul formel

Nous avons réalisé l'ensemble des fonctionnalitées indiquées dans le sujet.

En voici la liste : 

* **Evaluation** - 

	Il s'agit du calcul d'une expression ne contenant pas de variables en 	utilisant des nombres flottants. 
	
	On peut présenter le résultat de deux façons, une première donnant le r	ésultat final directement mais également une représentation par étapes du 	calcul permettant de 	dérouler les différentes opérations jusqu'à 	l'obtention du résultat final.
	
* **Substitution** - 
	 
	Cette fonctionnalité permet de substituer toutes les occurrences d'une 	variable (si elle existe) dans une expression donnée par une seconde 	expression.

* **Simplification** -

	Permet de simplifier une expression en une sous-expression simplifiée. 
	Nous avons choisi de simplifier des expressions en regroupant les termes 	similaires où encore en eliminant les plus grand diviseurs communs.
	
* **Solve** - 

	Notre outil permet de trouver pour quelle valeur s'annule une expression 	polynomiale du premier et du second degré.	
	
* **Derive** - 
	
	Calcul la dérivée d'une expression à une variable, peut-être appliqué sur 	l'ensemble des expressions connues.

* **Antiderive** - 

	Calcul l'integrale d'une expression à une variable. Nous avons choisi pour 	l'intégration les formules les plus connues.
	
* **Integrate** - 
	
	Permet, après avoir calculé l'intégrale d'une expression de l'intégrer entre 	deux bornes que l'on peut spécifier.
	
* **Plot** - 

	Présente une affichage d'une courbe représentant une expression à une 	variable. La courbe est dessinée sur l'intervalle [-5, 5] en abcisse et en 	ordonnée.

## Interface graphique 

Pour l'interface utilisteur, nous avons choisi d'implémenter une interface graphique à l'aide de la librairie Lablgtk3. Nous avons choisi de l'implémenter avec de la programmation orientée objet, la librairie étant fondée sur ce principe. Notre interface présente en quelque sorte une fenêtre par fonctionnalité, sauf concernant les manipulations de fonctions (derive, antiderive, simplification) qui elles sont regroupées dans une seule fenêtre.

## Installation 

Nous avons séparer notre projet en deux librairies 

* **`math`** contenant les fichiers relatifs à la partie fonctionnelle, le calcul formel et la manipulation d'expressions 

* **`graphic`** contenant les fichiers relatifs à l'interface utilisateur

Le fichier principal **`omatl`** se trouve à la racine du projet et peut se lancer à l'aide du script 

```bash 
./run
```

Nous avons également un directory de tests **`test`** dont les tests peuvent être lancés à laide de 

```bash 
make test
```
