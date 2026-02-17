# language: fr
@browser @admin-auth
Fonctionnalité: Authentification admin
  En tant qu'administrateur, je veux que l'accès au back-office soit sécurisé
  afin que seuls les utilisateurs authentifiés puissent y accéder.

  Scénario: L'accès au back-office est refusé sans authentification
    Quand je navigue vers la page admin sans authentification
    Alors je suis redirigé vers la page de login

  Scénario: Les routes publiques restent accessibles sans authentification
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand j'accède à la page d'accueil
    Alors je vois une grille de cartes produit en dessous du bandeau
