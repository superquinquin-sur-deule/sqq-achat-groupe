# language: fr
@browser @backoffice-orders
Fonctionnalité: Liste des commandes back-office
  En tant que membre de l'équipe logistique, je veux consulter la liste
  des commandes payées et leurs détails afin de préparer la distribution.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'il existe des commandes payées pour cette vente

  Scénario: Affichage du layout back-office et de la navigation
    Quand je navigue vers la page backoffice commandes
    Alors je vois le header back-office avec le titre "Back-office"
    Et je vois la navigation backoffice avec les liens Commandes, Bon fournisseur, Préparation, Distribution

  Scénario: Affichage de la liste des commandes
    Quand je navigue vers la page backoffice commandes
    Alors je vois le tableau des commandes backoffice
    Et chaque ligne affiche numéro, nom, email, créneau, montant et statut
    Et les badges de statut sont colorés correctement

  Scénario: Recherche par nom dans les commandes
    Quand je navigue vers la page backoffice commandes
    Et je saisis "Dupont" dans le champ de recherche backoffice
    Alors seules les commandes contenant "Dupont" sont affichées

  Scénario: Filtre par créneau
    Quand je navigue vers la page backoffice commandes
    Et je sélectionne un créneau dans le filtre backoffice
    Alors seules les commandes de ce créneau sont affichées

  Scénario: Détail d'une commande
    Quand je navigue vers la page backoffice commandes
    Et je clique sur une commande dans la liste backoffice
    Alors je vois les coordonnées du client
    Et je vois le créneau de retrait dans le détail backoffice
    Et je vois le tableau des produits avec quantités et prix
    Et je vois le total de la commande
