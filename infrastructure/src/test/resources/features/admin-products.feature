# language: fr
@browser @admin-products
Fonctionnalité: Gestion des produits (CRUD admin)
  En tant qu'administrateur, je veux gérer les produits du catalogue
  afin de configurer l'offre de l'achat groupé.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Lister tous les produits d'une vente
    Quand je navigue vers la page admin des produits
    Alors je vois le tableau des produits avec des lignes

  Scénario: Créer un nouveau produit
    Quand je navigue vers la page admin des produits
    Et je clique sur le bouton ajouter un produit
    Et je remplis le formulaire produit avec les valeurs
      | nom         | Carottes bio     |
      | prix HT     | 2.37             |
      | TVA         | 5.50             |
      | fournisseur | Ferme du Soleil  |
      | stock       | 30               |
      | référence   | CAR-002          |
      | catégorie   | Légumes          |
      | marque      | Ferme du Soleil  |
    Et je soumets le formulaire produit
    Alors je vois un toast de succès
    Et le produit "Carottes bio" apparaît dans le tableau

  Scénario: Modifier un produit existant
    Quand je navigue vers la page admin des produits
    Et je clique sur le bouton modifier du premier produit
    Et je modifie le nom du produit en "Tomates cerises bio"
    Et je soumets le formulaire produit en modification
    Alors je vois un toast de succès
    Et le produit "Tomates cerises bio" apparaît dans le tableau

  Scénario: Supprimer un produit
    Quand je navigue vers la page admin des produits
    Et je compte le nombre de produits dans le tableau
    Et je clique sur le bouton supprimer du premier produit
    Et je confirme la suppression
    Alors je vois un toast de succès
    Et le tableau contient un produit de moins

  Scénario: Créer un produit avec des données invalides
    Quand je navigue vers la page admin des produits
    Et je clique sur le bouton ajouter un produit
    Et je soumets le formulaire produit vide
    Alors je vois des messages d'erreur de validation

  Scénario: Modification produit bloquée quand la vente a des commandes
    Étant donné qu'une commande existe sur la vente via le navigateur
    Quand je navigue vers la page admin des produits
    Alors je vois le message d'avertissement commandes existantes
    Et le bouton modifier n'est pas visible

  Scénario: Désactivation produit autorisée quand la vente a des commandes
    Étant donné qu'une commande existe sur la vente via le navigateur
    Quand je navigue vers la page admin des produits
    Et je clique sur le bouton désactiver du premier produit
    Alors je vois un toast de succès

  Scénario: Suppression produit bloquée quand la vente a des commandes
    Étant donné qu'une commande existe sur la vente via le navigateur
    Quand je navigue vers la page admin des produits
    Alors le bouton supprimer n'est pas visible

  Scénario: Suppression vente bloquée quand elle a des commandes
    Étant donné qu'une commande existe sur la vente via le navigateur
    Quand je navigue vers la page admin ventes
    Alors le bouton supprimer de la vente n'est pas visible
