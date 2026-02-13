# language: fr
@browser @backoffice-supplier-order
Fonctionnalité: Bon de commande fournisseur
  En tant que membre de l'équipe logistique, je veux voir le bon de commande
  fournisseur agrégé afin de commander les bonnes quantités auprès de chaque fournisseur.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'il existe des commandes payées pour le bon fournisseur

  Scénario: Affichage du bon fournisseur agrégé groupé par fournisseur
    Quand je navigue vers la page bon fournisseur
    Alors je vois le titre bon fournisseur "Bon de commande fournisseur"
    Et je vois les produits groupés par fournisseur avec les quantités totales

  Scénario: Les boutons Imprimer et Exporter CSV sont visibles
    Quand je navigue vers la page bon fournisseur
    Alors je vois le bouton imprimer du bon fournisseur
    Et je vois le bouton exporter CSV du bon fournisseur

  Scénario: Export CSV télécharge un fichier
    Quand je navigue vers la page bon fournisseur
    Et je clique sur exporter CSV du bon fournisseur
    Alors un fichier CSV bon fournisseur est téléchargé

  @backoffice-supplier-order-empty
  Scénario: Aucune commande — état vide
    Étant donné une vente sans commande pour le bon fournisseur
    Quand je navigue vers la page bon fournisseur sans commande
    Alors je vois le message vide bon fournisseur "Aucune commande pour le moment"
