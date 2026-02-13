# language: fr
@browser @admin-campaign
Fonctionnalité: Gestion de la campagne (admin)
  En tant qu'administrateur, je veux activer ou désactiver la période de commande
  afin de piloter le cycle de vie de l'achat groupé.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Affichage de la campagne en état désactivé
    Étant donné que la campagne est désactivée
    Quand je navigue vers la page admin campagne
    Alors le toggle "Période de commande" est en position OFF
    Et je vois le message de campagne "Les commandes ne sont pas encore ouvertes"

  Scénario: Activer la campagne
    Étant donné que la campagne est désactivée
    Quand je navigue vers la page admin campagne
    Et je bascule le toggle de la campagne
    Alors je vois un toast de succès "Période de commande activée"
    Et le toggle "Période de commande" est en position ON

  Scénario: Désactiver la campagne
    Quand je navigue vers la page admin campagne
    Et le toggle "Période de commande" est en position ON
    Et je bascule le toggle de la campagne
    Alors je vois un toast de succès "Période de commande désactivée"
    Et le toggle "Période de commande" est en position OFF
