# language: fr
@browser
Fonctionnalité: Redirection des ventes expirées
  En tant que coopérateur,
  Quand j'accède à une vente dont la date de fin est passée,
  Je suis redirigé vers la liste des ventes.

  Scénario: Accéder à la page d'une vente expirée redirige vers la liste des ventes
    Étant donné qu'une vente expirée existe
    Quand j'accède à la page de la vente expirée
    Alors je suis redirigé vers la liste des ventes
