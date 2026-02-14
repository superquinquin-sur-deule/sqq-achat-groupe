# language: fr
@browser
Fonctionnalité: Page introuvable (404)
  En tant que visiteur,
  Quand j'accède à une URL qui n'existe pas,
  Je veux voir une page d'erreur claire avec un lien pour revenir à l'accueil.

  Scénario: Affichage de la page 404 pour une URL inconnue
    Quand j'accède à une URL inexistante
    Alors je vois la page introuvable
    Et la page affiche un bouton de retour à l'accueil

  Scénario: Le bouton de retour redirige vers l'accueil
    Quand j'accède à une URL inexistante
    Et je clique sur le bouton de retour à l'accueil
    Alors je suis redirigé vers la liste des ventes
