# language: fr
@browser @paiement-en-magasin
Fonctionnalité: Règlement en magasin
  En tant que coopérateur,
  Je veux valider ma commande sans payer en ligne,
  Afin de régler en magasin lors du retrait.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Le récapitulatif annonce le règlement en magasin
    Étant donné que j'ai des produits dans mon panier
    Et j'ai rempli mes coordonnées valides
    Et j'ai sélectionné un créneau disponible
    Alors le récapitulatif indique que le règlement se fait en magasin
    Et le bouton de validation affiche "Valider ma commande"

  Scénario: Valider la commande la confirme immédiatement sans paiement en ligne
    Étant donné que j'ai des produits dans mon panier
    Et j'ai rempli mes coordonnées valides
    Et j'ai sélectionné un créneau disponible
    Quand je confirme ma commande
    Alors je suis redirigé vers la confirmation
    Et la page de confirmation indique que le règlement se fait en magasin
    Et la commande confirmée est en statut "CONFIRMED"

  Scénario: L'API de paiement en ligne n'existe plus
    Quand j'appelle l'ancienne API d'initiation de paiement
    Alors l'API répond 404
