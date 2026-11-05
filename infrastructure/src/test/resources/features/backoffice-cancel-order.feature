# language: fr
@backoffice-cancel-order
Fonctionnalité: Annulation d'une commande depuis l'administration
  En tant qu'administrateur, je veux annuler une commande d'un client
  afin de restituer le stock des produits et de libérer la place du créneau.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et une commande confirmée existe sur le premier créneau avec 2 unités du premier produit

  Scénario: L'annulation restitue le stock et libère la place du créneau
    Quand j'annule la commande via l'API admin avec la raison "Demande du client"
    Alors l'annulation renvoie le statut 204
    Et la commande est annulée avec la raison "Demande du client"
    Et le stock du premier produit est de 25
    Et le premier créneau compte 0 place réservée

  Scénario: La raison de l'annulation est obligatoire
    Quand j'annule la commande via l'API admin sans raison
    Alors l'annulation renvoie le statut 400
    Et la commande est toujours confirmée
    Et le stock du premier produit est de 23

  Scénario: Impossible d'annuler une commande déjà récupérée
    Étant donné que la commande est marquée comme récupérée
    Quand j'annule la commande via l'API admin avec la raison "Trop tard"
    Alors l'annulation renvoie le statut 409
    Et le stock du premier produit est de 23

  Scénario: Impossible d'annuler une commande qui n'appartient pas à la vente
    Étant donné qu'une vente sans commande existe
    Quand j'annule la commande via l'API admin sur une autre vente
    Alors l'annulation renvoie le statut 404
    Et la commande est toujours confirmée

  Scénario: Annuler deux fois ne restitue le stock qu'une seule fois
    Quand j'annule la commande via l'API admin avec la raison "Demande du client"
    Et j'annule la commande via l'API admin avec la raison "Deuxième tentative"
    Alors l'annulation renvoie le statut 204
    Et la commande est annulée avec la raison "Demande du client"
    Et le stock du premier produit est de 25
