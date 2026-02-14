# language: fr
@browser @admin-timeslots
Fonctionnalité: Gestion des créneaux de retrait (admin)
  En tant qu'administrateur, je veux gérer les créneaux de retrait
  afin d'organiser la journée de distribution.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Affichage de la liste des créneaux
    Quand je navigue vers la page admin créneaux
    Alors je vois la table des créneaux avec les colonnes Date, Horaire, Capacité, Réservations et Actions
    Et chaque ligne affiche les informations du créneau

  Scénario: Créer un nouveau créneau
    Quand je navigue vers la page admin créneaux
    Et je clique sur "Ajouter un créneau"
    Et je remplis le formulaire créneau avec une date future, de 09:00 à 10:00 et une capacité de 20
    Et je clique sur le bouton "Enregistrer" du formulaire créneau
    Alors je vois un toast de succès "Créneau créé"
    Et la table des créneaux contient un créneau de plus

  Scénario: Modifier un créneau existant
    Quand je navigue vers la page admin créneaux
    Et je clique sur "Modifier" du premier créneau
    Et je modifie la capacité du créneau à 50
    Et je clique sur le bouton "Enregistrer" du formulaire créneau
    Alors je vois un toast de succès "Créneau mis à jour"

  Scénario: Supprimer un créneau sans réservation
    Quand je navigue vers la page admin créneaux
    Et je clique sur "Supprimer" du premier créneau
    Et je confirme la suppression du créneau
    Alors je vois un toast de succès "Créneau supprimé"
    Et la table des créneaux contient un créneau de moins

  Scénario: Tentative de suppression d'un créneau avec réservations
    Étant donné qu'un créneau a des réservations
    Quand je navigue vers la page admin créneaux
    Et je clique sur "Supprimer" du créneau avec réservations
    Alors je vois un avertissement de réservations sur le créneau

  Scénario: Validation des données invalides côté client
    Quand je navigue vers la page admin créneaux
    Et je clique sur "Ajouter un créneau"
    Et je soumets le formulaire créneau sans remplir les champs
    Alors je vois des erreurs de validation sur le formulaire créneau
