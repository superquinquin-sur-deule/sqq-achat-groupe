# language: fr
@admin-auth
Fonctionnalité: Authentification admin
  En tant qu'administrateur, je veux que l'accès au back-office soit sécurisé
  afin que seuls les utilisateurs authentifiés puissent y accéder.

  # Note : en mode test (OIDC désactivé), Quarkus retourne 403 (Forbidden) pour les endpoints protégés.
  # En production (OIDC actif), une requête JSON non authentifiée retourne 401 (Unauthorized).
  # Le test vérifie que l'accès est bien refusé sans authentification (AC #4).
  Scénario: L'accès à l'endpoint admin est refusé sans authentification
    Quand j'appelle l'endpoint admin me sans authentification
    Alors l'accès est refusé

  Scénario: Les routes publiques restent accessibles sans authentification
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je consulte la liste des produits
    Alors je reçois uniquement les produits actifs
