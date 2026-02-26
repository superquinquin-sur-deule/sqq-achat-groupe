# language: fr
@browser
Fonctionnalité: Catalogue produits frontend
  En tant que coopérateur, je veux parcourir le catalogue des produits disponibles
  afin de découvrir les produits proposés pour l'achat groupé.

  Scénario: Produits groupés par rayon
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors les produits sont groupés par rayon avec un titre par groupe

  Scénario: Contenu des cartes produit
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors chaque carte affiche le nom du produit
    Et chaque carte affiche la description du produit
    Et chaque carte affiche le prix
    Et chaque carte affiche la marque
    Et chaque carte affiche un bouton Ajouter

  Scénario: Navigation par rayon
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors je vois la barre de navigation par rayon
    Et chaque rayon a un bouton dans la barre de navigation

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

  Scénario: Affichage du hero banner avec les informations de la vente
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors je vois le hero banner avec les informations de la vente

  Scénario: Skeleton screens pendant le chargement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand la page d'accueil est en cours de chargement
    Alors je vois des skeleton screens à la place des cartes
