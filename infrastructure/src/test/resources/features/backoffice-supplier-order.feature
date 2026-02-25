# language: fr
@browser @backoffice-supplier-order
Fonctionnalité: Bon de commande fournisseur
  En tant que membre de l'équipe logistique, je veux voir le bon de commande
  fournisseur agrégé afin de commander les bonnes quantités auprès de chaque fournisseur.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et les commandes suivantes existent
      | prénom | nom    | email         | téléphone  | créneau | produits | statut |
      | Alice  | Durand | alice@test.fr | 0601020304 | 0       | 0:3,1:2  | PAID   |
      | Bob    | Martin | bob@test.fr   | 0605060708 | 0       | 0:1,2:4  | PAID   |

  Scénario: Affichage du bon fournisseur agrégé groupé par fournisseur
    Quand je navigue vers la page bon fournisseur
    Alors je vois le titre bon fournisseur "Bon de commande fournisseur"
    Et je vois les produits groupés par fournisseur avec les quantités totales

  Scénario: Les boutons Imprimer et Exporter Excel sont visibles
    Quand je navigue vers la page bon fournisseur
    Alors je vois le bouton imprimer du bon fournisseur
    Et je vois le bouton exporter Excel du bon fournisseur

  Scénario: Export Excel télécharge un fichier
    Quand je navigue vers la page bon fournisseur
    Et je clique sur exporter Excel du bon fournisseur
    Alors un fichier Excel bon fournisseur est téléchargé

  @backoffice-supplier-order-empty
  Scénario: Aucune commande — état vide
    Étant donné une vente sans commande existe
    Quand je navigue vers la page bon fournisseur sans commande
    Alors je vois le message vide bon fournisseur "Aucune commande pour le moment"
