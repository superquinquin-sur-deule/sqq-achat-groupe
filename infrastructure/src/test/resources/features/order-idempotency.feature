# language: fr
Fonctionnalité: Idempotence de création de commande
  En tant que coopérateur,
  Je ne veux pas créer de commande en double si je clique deux fois sur "Payer",
  Afin que mon stock et mon créneau ne soient pas impactés deux fois.

  Scénario: Soumettre deux fois la même commande avec la même clé d'idempotence retourne la même commande
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je crée une commande avec une clé d'idempotence
    Et je recrée la même commande avec la même clé d'idempotence
    Alors les deux réponses contiennent le même identifiant de commande
