# language: fr
@browser @backoffice-reception
Fonctionnalité: Réception des fournisseurs et ajustements
  En tant qu'administrateur, je veux enregistrer les réceptions fournisseurs,
  visualiser les manques et lancer les remboursements si nécessaire.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et les commandes suivantes existent
      | prénom | nom    | email            | téléphone  | créneau | produits | statut |
      | Alice  | Durand | alice@test.fr    | 0601020304 | 0       | 0:3,1:2  | PAID   |
      | Bob    | Martin | bob@test.fr      | 0605060708 | 0       | 0:1,2:4  | PAID   |
      | Claire | Petit  | claire@test.fr   | 0609101112 | 1       | 1:1,2:2  | PAID   |

  Scénario: Afficher le statut des réceptions pour une vente
    Quand je navigue vers la page réception
    Alors je vois le titre réception "Réception"
    Et je vois la liste des fournisseurs avec leur statut

  Scénario: Enregistrer une réception complète sans rupture
    Quand je navigue vers la page réception
    Et je clique sur saisir réception pour le premier fournisseur
    Alors je vois le formulaire de réception
    Quand je valide la réception avec les quantités par défaut
    Alors le fournisseur est marqué comme reçu

  Scénario: Enregistrer une réception avec rupture et voir les manques
    Étant donné que toutes les réceptions sont enregistrées avec une rupture sur le premier produit
    Quand je navigue vers la page réception
    Alors je vois l'aperçu des manques
    Et je vois le bouton appliquer les ajustements

  Scénario: Modifier une réception enregistrée
    Étant donné que toutes les réceptions sont enregistrées avec une rupture sur le premier produit
    Quand je navigue vers la page réception
    Et je clique sur modifier pour le premier fournisseur
    Alors je vois le formulaire de modification de réception
    Quand je valide la réception avec les quantités par défaut
    Alors le fournisseur est marqué comme reçu
