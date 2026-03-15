# language: fr
@browser @payment-race-condition
Fonctionnalité: Protection contre l'annulation pendant le paiement
  En tant que coopérateur,
  Je veux que ma commande soit confirmée même si elle a été annulée par erreur pendant le paiement,
  Afin de ne pas perdre mon achat alors que j'ai été débité.

  Scénario: Une commande annulée est réactivée quand le paiement Stripe réussit
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'une commande en attente de paiement existe
    Et la commande a été annulée entre-temps
    Quand le webhook Stripe notifie un paiement réussi
    Alors la page de confirmation affiche le statut payé
