# language: fr
@browser
Fonctionnalité: Catalogue produits frontend
  En tant que coopérateur, je veux parcourir le catalogue des produits disponibles
  afin de découvrir les produits proposés pour l'achat groupé.

  Scénario: Affichage du bandeau d'accueil et de la grille de produits
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors je vois le bandeau d'accueil jaune avec le titre de l'achat groupé
    Et je vois une grille de cartes produit en dessous du bandeau

  Scénario: Contenu des cartes produit
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors chaque carte affiche le nom du produit
    Et chaque carte affiche le fournisseur
    Et chaque carte affiche la description du produit
    Et chaque carte affiche le prix
    Et chaque carte affiche un bouton Ajouter

  Scénario: Produit épuisé affiché comme indisponible
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors la carte du produit épuisé est grisée
    Et la carte du produit épuisé affiche la mention Épuisé
    Et le bouton Ajouter du produit épuisé est désactivé

  Scénario: Affichage responsive mobile en 1 colonne
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil en mode mobile
    Alors les cartes s'affichent en 1 colonne

  Scénario: Affichage responsive desktop en 3 colonnes
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil en mode desktop
    Alors les cartes s'affichent en 3 colonnes

  Scénario: Skeleton screens pendant le chargement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand la page d'accueil est en cours de chargement
    Alors je vois des skeleton screens à la place des cartes
