# language: fr
@browser @backoffice-preparation
Fonctionnalité: Listes de préparation
  En tant que membre de l'équipe logistique, je veux voir les listes de préparation
  par commande organisées par créneau afin de reconstituer les paniers individuels.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et les commandes suivantes existent
      | prénom | nom    | email            | téléphone  | créneau | produits | statut |
      | Alice  | Durand | alice@test.fr    | 0601020304 | 0       | 0:3,1:2  | PAID   |
      | Bob    | Martin | bob@test.fr      | 0605060708 | 0       | 0:1,2:4  | PAID   |
      | Claire | Petit  | claire@test.fr   | 0609101112 | 1       | 1:1,2:2  | PAID   |

  Scénario: Affichage des fiches de préparation groupées par créneau
    Quand je navigue vers la page listes de préparation
    Alors je vois le titre préparation "Listes de préparation"
    Et je vois les fiches de préparation groupées par créneau
    Et chaque fiche contient le numéro de commande, le nom du coopérateur et les produits

  Scénario: Produits groupés par fournisseur dans les fiches
    Quand je navigue vers la page listes de préparation
    Alors les produits sont groupés par fournisseur dans les fiches de préparation

  Scénario: Filtre par créneau
    Quand je navigue vers la page listes de préparation
    Et je sélectionne un créneau dans le filtre préparation
    Alors seules les fiches du créneau sélectionné sont affichées

  Scénario: Le bouton Imprimer est visible
    Quand je navigue vers la page listes de préparation
    Alors je vois le bouton imprimer des listes de préparation

  Scénario: Produits manquants affichés après rupture de stock
    Étant donné toutes les réceptions sont enregistrées avec une rupture sur le premier produit
    Et les ajustements de rupture sont appliqués
    Quand je navigue vers la page listes de préparation
    Alors je vois les produits manquants sur les fiches de préparation

  @backoffice-preparation-empty
  Scénario: Aucune commande — état vide
    Étant donné une vente sans commande existe
    Quand je navigue vers la page listes de préparation sans commande
    Alors je vois le message vide préparation "Aucune commande pour le moment"
