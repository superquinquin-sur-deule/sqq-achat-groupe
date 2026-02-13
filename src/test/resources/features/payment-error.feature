# language: fr
@payment-error
Fonctionnalité: Gestion des erreurs de paiement
  En tant que coopérateur,
  Je veux pouvoir réessayer en cas d'échec de paiement et être informé clairement,
  Afin que je ne panique pas et puisse finaliser ma commande.

  Scénario: La page d'erreur affiche un message calme après un échec de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je consulte le statut de paiement de la commande
    Alors le statut de paiement indique 1 tentative sur 2
    Et le paiement peut être réessayé

  Scénario: Le retry crée une nouvelle session de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je retente le paiement
    Alors une nouvelle session de paiement est créée
    Et le nombre de tentatives est de 2

  Scénario: La commande est annulée après 2 échecs de paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec deux paiements échoués existe
    Quand je consulte le statut de paiement de la commande
    Alors le statut de la commande est "CANCELLED"
    Et les stocks des produits sont restaurés
    Et le créneau de retrait est libéré
    Et le paiement ne peut plus être réessayé

  Scénario: Le endpoint payment-status retourne les bonnes informations
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande avec un premier paiement échoué existe
    Quand je consulte le statut de paiement de la commande
    Alors la réponse contient le nombre de tentatives et le statut
