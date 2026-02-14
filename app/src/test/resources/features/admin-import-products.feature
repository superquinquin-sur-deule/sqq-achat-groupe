# language: fr
@browser @admin-import
Fonctionnalité: Import en masse de produits (admin)
  En tant qu'administrateur, je veux importer plusieurs produits via un fichier CSV
  afin de configurer le catalogue rapidement.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Affichage du formulaire d'import
    Quand je navigue vers la page admin produits
    Et je clique sur "Importer des produits"
    Alors je vois le formulaire d'import CSV

  Scénario: Import CSV valide
    Quand je navigue vers la page admin produits
    Et je clique sur "Importer des produits"
    Et j'uploade le fichier CSV "csv/valid-products.csv"
    Et je clique sur le bouton "Importer"
    Alors je vois un toast de succès d'import
    Et la liste des produits est rafraîchie

  Scénario: Import CSV avec lignes invalides (import partiel)
    Quand je navigue vers la page admin produits
    Et je clique sur "Importer des produits"
    Et j'uploade le fichier CSV "csv/mixed-valid-invalid.csv"
    Et je clique sur le bouton "Importer"
    Alors je vois un toast d'avertissement d'import
    Et je vois le rapport d'erreurs avec le tableau des détails

  Scénario: Import fichier CSV vide
    Quand je navigue vers la page admin produits
    Et je clique sur "Importer des produits"
    Et j'uploade le fichier CSV "csv/empty.csv"
    Et je clique sur le bouton "Importer"
    Alors je vois un toast d'erreur d'import

  Scénario: Import fichier au format non-CSV
    Quand je navigue vers la page admin produits
    Et je clique sur "Importer des produits"
    Et j'uploade le fichier "csv/invalid-format.txt"
    Et je clique sur le bouton "Importer"
    Alors je vois un toast d'erreur d'import
