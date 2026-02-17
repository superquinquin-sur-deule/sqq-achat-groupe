# language: fr
@browser
Fonctionnalité: Panier d'achat
  En tant que coopérateur,
  Je veux ajouter des produits à mon panier et gérer mes quantités,
  Afin de constituer ma commande avant de passer au paiement.

  Scénario: Ajouter un produit au panier depuis le catalogue
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je navigue vers la page d'accueil
    Et je clique sur "Ajouter" pour le premier produit disponible
    Alors le produit est ajouté à mon panier
    Et un toast "Produit ajouté" apparaît

  Scénario: Modifier la quantité d'un produit dans le panier
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand je consulte mon panier
    Et j'augmente la quantité du produit
    Alors le sous-total de la ligne et le total général se mettent à jour

  Scénario: Supprimer un produit du panier
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand je consulte mon panier
    Et je clique sur le bouton supprimer du produit
    Alors le produit est retiré du panier
    Et le total se met à jour

  Scénario: Consulter le récapitulatif du panier
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté plusieurs produits au panier depuis le catalogue
    Quand je consulte mon panier
    Alors je vois chaque produit avec son nom, prix unitaire, quantité et sous-total
    Et je vois le total général en bas

  Scénario: Le compteur du header affiche le nombre d'articles
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je navigue vers la page d'accueil
    Et j'ajoute des produits au panier
    Alors le compteur du bouton panier dans le header affiche le nombre correct d'articles

  Scénario: Le panier est conservé après rechargement de la page
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand je recharge la page
    Alors le produit est toujours dans mon panier
    Quand je consulte mon panier
    Alors je vois chaque produit avec son nom, prix unitaire, quantité et sous-total

  Scénario: Le bouton Ajouter est remplacé par un sélecteur de quantité après ajout
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je navigue vers la page d'accueil
    Et je clique sur "Ajouter" pour le premier produit disponible
    Alors le bouton "Ajouter" du premier produit est remplacé par un sélecteur de quantité
    Et le sélecteur affiche la quantité 1

  Scénario: Augmenter la quantité d'un produit depuis le catalogue
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand j'augmente la quantité du produit depuis le catalogue
    Alors le sélecteur affiche la quantité 2
    Et le compteur du panier dans le header affiche 2

  Scénario: Retirer un produit du panier en diminuant la quantité à zéro depuis le catalogue
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand je diminue la quantité du produit depuis le catalogue
    Alors le bouton "Ajouter" réapparaît sur le premier produit
    Et le compteur du panier dans le header n'est plus visible

  Scénario: Continuer mes achats depuis le panier rempli
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai ajouté un produit au panier depuis le catalogue
    Quand je consulte mon panier
    Et je clique sur "Continuer mes achats"
    Alors je suis redirigé vers le catalogue

  Scénario: Panier vide avec message et lien vers le catalogue
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je navigue vers la page panier
    Alors le panier affiche le message "Votre panier est vide"
    Et je vois un bouton "Découvrir nos produits"
    Quand je clique sur "Découvrir nos produits"
    Alors je suis redirigé vers le catalogue
