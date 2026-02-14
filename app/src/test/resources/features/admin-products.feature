# language: fr
@admin-products
Fonctionnalité: Gestion des produits (CRUD admin)
  En tant qu'administrateur, je veux gérer les produits du catalogue
  afin de configurer l'offre de l'achat groupé.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Lister tous les produits d'une vente
    Quand je liste les produits admin de la vente
    Alors je reçois la liste de tous les produits
    Et chaque produit contient les champs id, venteId, name, price, supplier, stock et active

  Scénario: Créer un nouveau produit
    Quand je crée un produit admin avec le nom "Carottes bio" au prix de 2.50 du fournisseur "Ferme du Soleil" avec un stock de 30
    Alors le produit est créé avec succès
    Et le produit créé a le nom "Carottes bio"
    Et le produit créé est actif

  Scénario: Modifier un produit existant
    Quand je modifie le premier produit avec le nom "Tomates cerises bio" et le prix 4.20
    Alors le produit est modifié avec succès
    Et le produit modifié a le nom "Tomates cerises bio"

  Scénario: Supprimer un produit
    Quand je supprime le premier produit
    Alors le produit est supprimé avec succès
    Et la liste des produits admin a un produit de moins

  Scénario: Créer un produit avec des données invalides
    Quand je crée un produit admin avec un nom vide
    Alors je reçois une erreur de validation 400
