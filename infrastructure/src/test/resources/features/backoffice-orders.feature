# language: fr
@browser @backoffice-orders
Fonctionnalité: Liste des commandes back-office
  En tant que membre de l'équipe logistique, je veux consulter la liste
  des commandes payées et leurs détails afin de préparer la distribution.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et les commandes suivantes existent
      | prénom | nom    | email            | téléphone  | créneau | produits | statut |
      | Marie  | Dupont | marie@exemple.fr | 0612345678 | 0       | 0:2      | PAID   |
      | Jean   | Martin | jean@exemple.fr  | 0698765432 | 1       | 0:1      | PAID   |

  Scénario: Affichage de la sidenav d'administration unifiée avec sélecteur de vente
    Quand je navigue vers la page backoffice commandes
    Alors je vois la sidenav d'administration
    Et la sidenav contient les liens suivants
      | sidenav-dashboard      |
      | sidenav-products       |
      | sidenav-timeslots      |
      | sidenav-ventes         |
      | sidenav-vente-selector |
      | sidenav-orders         |
      | sidenav-supplier       |
      | sidenav-preparation    |
      | sidenav-distribution   |

  Scénario: Affichage de la liste des commandes
    Quand je navigue vers la page backoffice commandes
    Alors je vois le tableau des commandes backoffice
    Et chaque ligne affiche numéro, date, nom, email, créneau, montant et statut
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
