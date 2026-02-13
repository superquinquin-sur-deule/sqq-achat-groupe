# language: fr
@browser @admin-ventes
Fonctionnalité: Gestion des ventes (admin)
  En tant qu'administrateur, je veux gérer les ventes
  afin de piloter le cycle de vie de l'achat groupé.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Affichage de la liste des ventes
    Quand je navigue vers la page admin ventes
    Alors je vois le tableau des ventes
    Et chaque ligne affiche nom, dates et statut

  Scénario: Création d'une vente
    Quand je navigue vers la page admin ventes
    Et je clique sur "Créer une vente"
    Et je remplis le formulaire vente avec le nom "Vente Printemps"
    Et je clique sur le bouton "Enregistrer" du formulaire vente
    Alors je vois un toast de succès "Vente créée"

  Scénario: Désactivation d'une vente
    Quand je navigue vers la page admin ventes
    Et je clique sur le bouton désactiver de la première vente
    Alors je vois un toast de succès "Vente désactivée"

  Scénario: Activation d'une vente fermée
    Étant donné que la vente est désactivée
    Quand je navigue vers la page admin ventes
    Et je clique sur le bouton activer de la première vente
    Alors je vois un toast de succès "Vente activée"

  Scénario: Le sélecteur de vente est visible dans la sidenav
    Quand je navigue vers la page admin ventes
    Alors le sélecteur de vente est visible dans la sidenav
