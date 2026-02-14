# language: fr
@browser
Fonctionnalité: Formulaire de commande et sélection de créneau
  En tant que coopérateur,
  Je veux saisir mes coordonnées et choisir un créneau de retrait,
  Afin que ma commande soit enregistrée et mes produits réservés.

  Scénario: Le formulaire de commande affiche les champs requis
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai des produits dans mon panier
    Quand je navigue vers le checkout
    Alors je vois le Stepper à l'étape 2 "Coordonnées"
    Et je vois les champs "Nom complet", "Adresse email" et "Téléphone"

  Scénario: La validation affiche des messages d'erreur bienveillants
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai des produits dans mon panier
    Quand je navigue vers le checkout
    Et je clique sur "Continuer" sans remplir le formulaire
    Alors je vois des messages d'erreur sous les champs invalides

  Scénario: Les créneaux disponibles sont affichés avec les places restantes
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai des produits dans mon panier
    Et j'ai rempli mes coordonnées valides
    Quand je passe à l'étape créneau
    Alors je vois les créneaux disponibles avec le nombre de places restantes

  Scénario: Un créneau complet est grisé et non cliquable
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai des produits dans mon panier
    Et un créneau est complet
    Et j'ai rempli mes coordonnées valides
    Quand je passe à l'étape créneau
    Alors le créneau complet est grisé avec la mention "Complet"

  Scénario: Créer une commande avec succès
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai des produits dans mon panier
    Et j'ai rempli mes coordonnées valides
    Et j'ai sélectionné un créneau disponible
    Quand je confirme ma commande
    Alors la commande est créée avec succès
    Et je suis redirigé vers la confirmation

  Scénario: Erreur de stock insuffisant
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et j'ai un produit en quantité supérieure au stock dans mon panier
    Et j'ai rempli mes coordonnées valides
    Et j'ai sélectionné un créneau disponible
    Quand je confirme ma commande
    Alors je vois un message d'erreur indiquant le stock insuffisant

  Scénario: Redirection si panier vide
    Étant donné qu'une vente existe avec des produits et des créneaux
    Et mon panier est vide
    Quand j'essaie d'accéder au checkout directement
    Alors je suis redirigé vers la page d'accueil
