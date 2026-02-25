# language: fr
@browser @backoffice-distribution
Fonctionnalité: Distribution et suivi des retraits
  En tant que membre de l'équipe logistique, je veux suivre les retraits
  par créneau et marquer les commandes récupérées.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et les commandes suivantes existent
      | prénom | nom    | email            | téléphone  | créneau | produits | statut    |
      | Alice  | Durand | alice@test.fr    | 0601020304 | 0       | 0:3,1:2  | PAID      |
      | Bob    | Martin | bob@test.fr      | 0605060708 | 0       | 0:1,2:4  | PAID      |
      | Claire | Petit  | claire@test.fr   | 0609101112 | 1       | 1:1,2:2  | PAID      |
      | Diana  | Lopez  | diana@test.fr    | 0610111213 | 1       | 0:2      | PICKED_UP |

  Scénario: Affichage de la page distribution avec filtres par créneau
    Quand je navigue vers la page distribution
    Alors je vois le titre distribution "Distribution"
    Et je vois les boutons de filtre par créneau
    Et je vois toutes les commandes avec leur statut

  Scénario: Filtrage par créneau
    Quand je navigue vers la page distribution
    Et je clique sur un bouton de créneau dans le filtre distribution
    Alors seules les commandes du créneau sélectionné sont affichées dans la distribution

  Scénario: Recherche par nom
    Quand je navigue vers la page distribution
    Et je saisis "Alice" dans le champ de recherche distribution
    Alors seules les commandes correspondant au nom "Alice" sont affichées dans la distribution

  Scénario: Marquer une commande comme récupérée
    Quand je navigue vers la page distribution
    Et je clique sur "Marquer récupéré" pour la première commande payée
    Alors le statut de la commande distribution passe à "Récupéré"
    Et le bouton "Marquer récupéré" disparaît pour cette commande distribution

  Scénario: Filtre non récupérées
    Quand je navigue vers la page distribution
    Et je clique sur le bouton "Non récupérées" dans la distribution
    Alors seules les commandes en statut "Payé" sont affichées dans la distribution

  Scénario: Bouton imprimer PDF visible
    Quand je navigue vers la page distribution
    Alors je vois le bouton imprimer PDF distribution

  @backoffice-distribution-empty
  Scénario: Aucune commande — état vide
    Étant donné une vente sans commande existe
    Quand je navigue vers la page distribution sans commande
    Alors je vois le message vide distribution "Aucune commande pour le moment"
