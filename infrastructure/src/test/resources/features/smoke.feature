# language: fr
@browser
Fonctionnalité: Smoke test
  Vérification que l'application démarre correctement

  Scénario: L'application est accessible
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors je vois une grille de cartes produit en dessous du bandeau
