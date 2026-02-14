# language: fr
@browser @admin-dashboard
Fonctionnalité: Dashboard administrateur
  En tant qu'administrateur, je veux consulter les statistiques de la vente
  afin de suivre l'avancement de l'achat groupé.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Affichage des statistiques du dashboard
    Quand je navigue vers la page admin dashboard
    Alors je vois la zone des statistiques
    Et je vois la carte "Commandes"
    Et je vois la carte "Montant total"
    Et je vois la carte "Taux de retrait"
    Et je vois la carte "Panier moyen"
    Et je vois le tableau de répartition par créneau
