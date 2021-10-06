#  - Les règles de nos différents jeux de cartes - 

# La bataille

## Les objets 

* Cartes `classiques` de type `valeur|couleur|forme` (ex : `As, rouge, pique` ).

*  **Valeur** de `1 à 13`, **Couleur** `Rouge|Noir`, **Forme** `Coeur|Trèfle|Carreaux|Pique`.

* Un **Deck** de **52** cartes `classiques` (où **54** s'il contient des jokers).

* **2** joueurs seulement.

## Le déroulement du jeu 

* Distribution du **deck** de **52** cartes aux **2** joueurs, en alternant jusqu'a épuisement du deck.

#### Boucle du jeu

* **Chacuns des deux joueurs découvrent la carte du dessus de leurs mains**.

* **Comparaison de la valeur des deux cartes** 
 
	> - si **carte_1 > carte_2** le joueur 1 remporte les deux cartes.
	> - si **carte_1 < carte_2** le joueur 2 remporte les deux cartes.
	> - si **carte_1 = carte_2** il y a **BATAILLE**.
	
* **La bataille**
	
	> - Les deux joueurs rejouent tous les deux une cartes.
	> - Retour à la boucle principale. 

#### Conditions de victoire
		
* L'un des deux joueurs n'a plus aucunes cartes dans sa main.
		

# Le Uno

## Les objets 

* Cartes `Uno` de type `(valeur|couleur)` (ex : `7, Jaune`).

* Peut se jouer de **2** à **10** joueurs.

* Un **Deck** de **108** cartes `Uno`.

### - Les cartes `normales` -

* **19 cartes** de couleur **bleu**, numérotées de `0 à 9` *(2 pour chaque chiffre sauf pour le 0)*. 

* **19 cartes** de couleur **rouge**, numérotées de `0 à 9` *(2 pour chaque chiffre sauf pour le 0)*.

* **19 cartes** de couleur **jaune**, numérotées de `0 à 9` *(2 pour chaque chiffre sauf pour le 0)*.

* **19 cartes** de couleur **verte**, numérotées de `0 à 9` *(2 pour chaque chiffre sauf pour le 0)*.


### - Les cartes `spéciales` - 

* **8 cartes** `+2`, (2 pour chaque couleur).
> - Le joueur suivant pioche **4** cartes et passe son tour.

* **8 cartes** `Inversement de sens`, *(2 pour chaque couleur)*.
> - Le sens de la partie est **inversé**.

* **8 cartes** `Passe ton tour`, *(2 pour chaque couleur)*.
> - Le joueur suivant doit **passer** son tour.

* **4 cartes**  `Joker`.
> - Le joueur jouant cette carte peut choisir de **changer** la couleur où non.

* **4 cartes** `+4`.
> - Le joueur suivant doit piocher **4** cartes. De plus le joueur ayant joué la carte peut choisir où non de changer de couleur. 


## Le déroulement du jeu 

* Distribution de **7** cartes à chaque joueur, en alternant. Le reste du **deck** fera office de pioche. Enfin **une carte** est tiré et posée face retournée au centre, si c'est une **carte spéciale** elle applique son effet directement sur le prochain joueur.

#### Boucle du jeu 

* Le premier joueur doit recouvrir la carte de la pioche par une carte soit :

	> - **d’une même couleur**,

	> - **du même chiffre**,

	> - **du même symbole**.

* Le joueur ne peut pas jouer ? Il a la possibilité de poser une carte soit :

	>  - **joker**

	>  - **+4**

* Sinon, dans le cas ou le joueur ne possède aucune de ces cartes : 
	
	> - il doit en **piocher une**.
 

* Finalement, si cette carte peut être jouée, **il peut directement la poser**, sinon il devra la conserver dans son jeu.

#### Conditions de victoire

* Le premier des joueurs à s’être débarrassé de **toutes ses cartes gagne**. Un comptage est alors fait sur les cartes des joueurs restants.

* Pour gagner, il vous faudra marquer **500** points ou inversement, c’est-à-dire être celui à avoir le **moins de points** lorsque l’un des joueurs **dépasse les 500 points**.


#### Comptage des points


* Les cartes numérotées se comptent suivant leur **valeur**.
* La carte **`+2`** vaut **20 points**.
* La carte **`Inversement de sens`** vaut **20 points**.
* La carte **`Passe ton tour`** vaut **20 points**.
* La carte **`Joker`** vaut **50 points**.
* La carte **`+4`** vaut **50 points**.


# Le 8 Américain 

## Les objets 

* Cartes `classiques` de type `valeur|couleur|forme` (ex : `As, rouge, pique` ).

*  **Valeur** de `1 à 13`, **Couleur** `Rouge|Noir`, **Forme** `Coeur|Trèfle|Carreaux|Pique`.

* Un **Deck** de **52** cartes `classiques` (où **54** s'il contient des jokers).

* Peut se jouer de **2** joueurs à **5** joueurs.


### - Les cartes `normales` -

* Les cartes numérotées suivantes 

	> - **3, 4, 5, 6, 7, 9, 10, Dame, Roi**.


### - Les cartes `spéciales` - 

* Les `8` 
	> - permettent de **changer de couleur** à n’importe quel moment.

* Les `Jokers` 
	> - font **piocher + 4 cartes** au joueur suivant
	
* Les `Valets` 
	> - font **sauter le tour** du joueur suivant.

* Les `As` 
	> - font **changer le sens** du jeu.
	
* Les `2` 
	> - font font **piocher + 2 cartes** au joueur suivant.



## Le déroulement du jeu 

* Distribution de **7** cartes à chaque joueur, en alternant. Le reste du **deck** fera office de pioche. Enfin **une carte** est tiré et posée face retournée au centre, si c'est une **carte spéciale**, une autre est piochée.

#### Boucle du jeu 
	
* Le premier joueur doit recouvrir la carte de la pioche par une carte soit :

	> - **d’une même couleur**,

	> - **du même chiffre**,
	
	> - **une des cartes spéciales**.

* Sinon, dans le cas ou le joueur ne possède aucune de ces cartes : 
	
	> - il doit en **piocher une**.
 

* Finalement, si cette carte peut être jouée, **il peut directement la poser**, sinon il devra la conserver dans son jeu.

#### Conditions de victoire

* Le premier des joueurs à s’être débarrassé de **toutes ses cartes gagne**. Un comptage est alors fait sur les cartes des joueurs restants.

* La partie s’arrête quand le premier joueur arrive à **500 points**. Le joueur ayant le **moins de points** remporte la partie.


#### Comptage des points


* Les **`cartes normales`** se comptent suivant leur **valeur**.
* **`Roi ou Dame`** : **10 points**.
* **`Valet, AS, 2`** : **20 points**.
* **`8 et Joker`** : **50 points**.


# La Briscola

## Les objets 

## Le déroulement du jeu 

#### Boucle du jeu 

#### Conditions de victoire

#### Comptage des points



# La Scopa

## Les objets

## Le déroulement du jeu

#### Boucle du jeu 

#### Conditions de victoire

#### Comptage des points


<!--
# La belote 


## Les objets

* Cartes `classiques` de type `valeur|couleur|forme` (ex : `As, rouge, pique` ).

*  **Valeur** de `1` puis `7 à 13`, **Couleur** `Rouge|Noir`, **Forme** `Coeur|Trèfle|Carreaux|Pique`.

* Un **Deck** de **32** cartes `classiques`.

* Se joue à **4** joueurs, en équipe de **2**.


## Le déroulement du jeu

* Distribution qui se fait en **2** fois, le donneur doit donner en tout **5** cartes par **joueurs**. Une première fois en distribuant par **2** et une seconde fois en distribuant par **3**. Ensuite, la **21 ème** carte est posée face retournée.

#### Boucle du jeu 

##### Préambule 

* La carte retournée est considérée, dans un premier temps, comme **la couleur de l’atout**. 

* Le premier joueur situé après le donneur choisit, **si oui ou non**, il prend la carte. 

* Si le joueur ne prend pas, c’est au joueur suivant de donner son avis. 

* Si lors du tour de table l’un des joueurs **décide de prendre la carte retournée**, il la **met dans son jeu** et **la couleur de la carte** retournée **devient l’atout**.

	> - Le jeu commence. 

* Si **aucun joueur** n’a voulu prendre la carte retournée, le joueur qui a parlé en **premier** peut alors choisir d’annoncer **une autre couleur** ou **passer** une seconde fois.
 
* S’il ne dit rien, c’est au joueur suivant de dire si oui ou non, il choisit sa couleur d’atout. 

* A partir du moment où un **joueur a choisi sa couleur**, son équipe devient l’équipe des **attaquants** et l’autre l’équipe des **défenseurs**. 

	> - Le jeu commence.


* Pour finir, si aucun joueur ne prend lors du **premier et deuxième** tour de parole, les cartes sont a **redistribuer**.


##### Début du jeu 

* Lorsqu’un joueur décide de prendre, il reçoit **2 cartes supplémentaires** en plus de la carte retournée tandis que les autres joueurs reçoivent chacun **3 cartes supplémentaires**. Les joueurs possèdent donc **8 cartes** chacun.

* Le joueur suivant doit suivre la couleur demandée :  

	> - Si il peut, c'est au joueur suivant. 
	> - Sinon, il doit couper avec l'atout.
	> - Si il n'a pas d'atout, il doit jouer une carte dans une autre couleur.
	
* Le gagnant d'un **pli** le ramasse et débute la manche suivante.

* Dans le cas ou **deux joueurs coupent** : 
	> -  le second doit **obligatoirement surcouper**, c'est à dire fournir un 		**atout** plus fort que le précédent.
	> - si il n'a pas **d'atout plus fort** il doit jouer un atout plus bas si 		il en possède dans son jeu.

#### Conditions de victoire



#### Comptage des points


* Le jeu de **Belote** est un jeu à contrat ou l’équipe qui a pris doit amasser plus de points que l’autre équipe pour faire son contrat, c’est-à-dire au **minimum 82 points**.

	> - Si l’équipe **qui attaque** remplit son contrat en réalisant au 		**minimum 82** points, chaque équipe marque les points qu’elle a r		éalisés. 
	> - Au cas ou elle ne **remplit pas** son contrat, on dit qu’elle est 		dedans, et tous les points en jeu **(162 points)** reviendront à l’équipe 		**défenseurs**.
	
#### Les bonus, à ajouter ? 

* Lorsqu’un joueur possède **à la fois le roi et la dame d’atout**, il peut faire bénéficier un bonus de **20 points** à son équipe en disant a voix haute lors de leurs mises en jeu respectives : **`"belote et rebelote"`**. **Ses 20 points** appartiennent obligatoirement à l’équipe, même si elle n’a pas réalisé de plis lors de la partie.

* Ces points sont très importants puisqu’ils peuvent mettre une équipe dedans ou pas. En effet, si l’équipe de défenseurs obtient au minimum 72 points dans ses plis, en ajoutant la belote, cela donne 92 points. L’équipe d’attaque qui a pourtant 90 points est pourtant dedans et perd son contrat.

* Une égalité ? 

	> - Dans le cas ou les deux équipes amassent le même nombre de points 		**(81)**, l’équipe **d’attaque** ne marque **rien**, et l’équipe de 		**défense** marque **81 points**. Les 81 autres points seront données 
		à l	’équipe qui gagnera la partie suivante.
-->







