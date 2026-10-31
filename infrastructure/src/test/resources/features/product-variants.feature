# language: fr
@product-variants
Fonctionnalité: Variantes de produit
  En tant que coopérateur,
  Je veux choisir la déclinaison d'un produit (taille, format) avant de l'ajouter,
  Afin de commander exactement la variante souhaitée, à son prix propre.

  @browser
  Scénario: La carte d'un produit à variantes liste les déclinaisons avec leur prix
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Alors la carte du produit "Sapin Nordmann" affiche 3 variantes
    Et la variante "150cm" affiche le prix "30,00 €"
    Et la variante "200cm" affiche le prix "42,00 €"

  @browser
  Scénario: Aucune variante n'est sélectionnée au chargement
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Alors aucune variante du produit "Sapin Nordmann" n'est sélectionnée
    Et le bouton Ajouter du produit "Sapin Nordmann" est désactivé

  @browser
  Scénario: Choisir une variante active le bouton Ajouter
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Et je sélectionne la variante "200cm" du produit "Sapin Nordmann"
    Alors le bouton Ajouter du produit "Sapin Nordmann" est actif
    Quand je clique sur "Ajouter" pour le produit "Sapin Nordmann"
    Alors le panier contient la ligne "Sapin Nordmann — 200cm"

  @browser
  Scénario: Une variante épuisée ne peut pas être sélectionnée
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Alors la variante "250cm" du produit "Sapin Nordmann" est désactivée

  @browser
  Scénario: Deux variantes du même produit forment deux lignes de panier distinctes
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Et j'ajoute la variante "150cm" du produit "Sapin Nordmann" au panier
    Et j'ajoute la variante "200cm" du produit "Sapin Nordmann" au panier
    Et je consulte mon panier
    Alors le panier contient 2 lignes
    Et le panier contient la ligne "Sapin Nordmann — 150cm" au prix "30,00 €"
    Et le panier contient la ligne "Sapin Nordmann — 200cm" au prix "42,00 €"

  @browser
  Scénario: Un produit à variante unique garde le bouton Ajouter direct
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je navigue vers la page d'accueil
    Alors la carte du produit "Couronne de l'Avent" n'affiche pas de sélecteur de variante
    Et le bouton Ajouter du produit "Couronne de l'Avent" est actif

  Scénario: Le bon fournisseur détaille une ligne par variante
    Étant donné qu'une vente existe avec un produit à variantes
    Quand je commande 2 unités de la variante "150cm" et 3 unités de la variante "200cm"
    Alors le bon fournisseur contient une ligne "SAP-150" de 2 unités avec un colisage de 5
    Et le bon fournisseur contient une ligne "SAP-200" de 3 unités avec un colisage de 3
    Et le bon fournisseur nomme la ligne "SAP-200" "Sapin Nordmann — 200cm"
