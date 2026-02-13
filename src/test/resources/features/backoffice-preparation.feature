# language: fr
@browser @backoffice-preparation
Fonctionnalité: Listes de préparation
  En tant que membre de l'équipe logistique, je veux voir les listes de préparation
  par commande organisées par créneau afin de reconstituer les paniers individuels.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'il existe des commandes payées réparties sur plusieurs créneaux

  Scénario: Affichage des fiches de préparation groupées par créneau
    Quand je navigue vers la page listes de préparation
    Alors je vois le titre préparation "Listes de préparation"
    Et je vois les fiches de préparation groupées par créneau
    Et chaque fiche contient le numéro de commande, le nom du coopérateur et les produits

  Scénario: Filtre par créneau
    Quand je navigue vers la page listes de préparation
    Et je sélectionne un créneau dans le filtre préparation
    Alors seules les fiches du créneau sélectionné sont affichées

  Scénario: Le bouton Imprimer est visible
    Quand je navigue vers la page listes de préparation
    Alors je vois le bouton imprimer des listes de préparation

  @backoffice-preparation-empty
  Scénario: Aucune commande — état vide
    Étant donné une vente sans commande pour la préparation
    Quand je navigue vers la page listes de préparation sans commande
    Alors je vois le message vide préparation "Aucune commande pour le moment"
