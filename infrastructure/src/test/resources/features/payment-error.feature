# language: fr
@browser @payment-error
Fonctionnalité: Gestion des erreurs de paiement
  En tant que coopérateur,
  Je veux pouvoir réessayer en cas d'échec de paiement et être informé clairement,
  Afin que je ne panique pas et puisse finaliser ma commande.

  Scénario: La page d'erreur affiche un message calme après un échec de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je navigue vers la page d'erreur de paiement
    Alors je vois le conteneur d'erreur de paiement
    Et je vois le titre d'erreur "Le paiement n'a pas fonctionné"
    Et je vois le compteur de tentatives "Tentative 1/2"

  Scénario: Le retry crée une nouvelle session de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je navigue vers la page d'erreur de paiement
    Et je clique sur le bouton réessayer
    Alors je suis redirigé vers la page de paiement

  Scénario: La commande est annulée après 2 échecs de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec deux paiements échoués existe
    Quand je navigue vers la page d'erreur de paiement
    Alors je vois le conteneur de commande annulée
    Et je vois le titre d'annulation "Votre commande a été annulée"

  Scénario: La page d'erreur affiche les informations de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je navigue vers la page d'erreur de paiement
    Alors je vois le conteneur d'erreur de paiement
    Et le bouton réessayer est visible
