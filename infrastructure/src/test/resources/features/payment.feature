# language: fr
@browser @payment
Fonctionnalité: Paiement par carte bancaire
  En tant que coopérateur,
  Je veux payer ma commande par carte bancaire en ligne,
  Afin que ma commande soit confirmée et mes produits garantis.

  Scénario: La table payments existe après migration
    Étant donné que l'application est démarrée
    Quand je vérifie la structure de la base de données
    Alors la table payments existe avec les colonnes requises

  Scénario: Initier un paiement redirige vers Stripe Checkout
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et que j'ai ajouté des produits au panier pour le paiement
    Et que j'ai saisi mes coordonnées pour le paiement
    Et que j'ai choisi un créneau pour le paiement
    Quand je clique sur "Payer ma commande"
    Alors je suis redirigé vers la page de paiement

  Scénario: Webhook de paiement réussi met à jour la commande
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'une commande en attente de paiement existe
    Quand le webhook Stripe notifie un paiement réussi
    Alors le statut de la commande passe à "PAID"
    Et le paiement est enregistré avec le statut "SUCCEEDED"

  Scénario: Webhook idempotent ne duplique pas le paiement
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et qu'une commande a déjà été payée
    Quand le webhook Stripe notifie le même paiement une deuxième fois
    Alors la commande reste au statut "PAID"
    Et aucun doublon de paiement n'est créé

  Scénario: Page de confirmation affiche le récapitulatif
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et que j'ai ajouté des produits au panier pour le paiement
    Et que j'ai saisi mes coordonnées pour le paiement
    Et que j'ai choisi un créneau pour le paiement
    Et j'ai payé ma commande avec succès
    Quand je suis sur la page de confirmation
    Alors je vois le message "Merci pour votre commande !"
    Et je vois le récapitulatif de la commande avec les produits
    Et je vois le créneau de retrait
