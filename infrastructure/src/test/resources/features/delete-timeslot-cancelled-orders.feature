# language: fr
@delete-timeslot-cancelled-orders
Fonctionnalité: Suppression d'un créneau avec commandes annulées
  En tant qu'administrateur, je veux pouvoir supprimer un créneau
  dont toutes les commandes ont été annulées.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Supprimer un créneau dont toutes les commandes sont annulées
    Étant donné qu'une commande existe sur le premier créneau
    Et la commande est annulée
    Quand je supprime le premier créneau via l'API admin
    Alors la suppression du créneau réussit avec le statut 204

  Scénario: Impossible de supprimer un créneau avec une commande active
    Étant donné qu'une commande existe sur le premier créneau
    Quand je supprime le premier créneau via l'API admin
    Alors la suppression du créneau échoue avec le statut 409
