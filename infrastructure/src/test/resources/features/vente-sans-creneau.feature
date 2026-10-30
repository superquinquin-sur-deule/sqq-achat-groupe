# language: fr
@vente-sans-creneau
Fonctionnalité: Vente sans créneau de réservation
  En tant qu'administrateur, je veux configurer une vente dont le retrait se fait
  sur une simple plage de dates, afin que les coopérateurs n'aient aucun créneau à choisir.

  @browser
  Scénario: Création d'une vente en mode plage de dates
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je navigue vers la page admin ventes
    Et je clique sur "Créer une vente"
    Et je remplis le formulaire vente sans créneau avec le nom "Vente Plage"
    Et je clique sur le bouton "Enregistrer" du formulaire vente
    Alors je vois un toast de succès "Vente créée"

  @browser
  Scénario: L'onglet Créneaux est désactivé pour une vente sans créneau
    Étant donné qu'une vente sans créneau existe avec des produits
    Quand je navigue vers la page admin créneaux de la vente sans créneau
    Alors je vois le message expliquant que la vente fonctionne en plage de dates
    Et le lien Créneaux de la sidenav est désactivé

  @browser
  Scénario: Le bandeau annonce la plage de retrait
    Étant donné qu'une vente sans créneau existe avec des produits
    Quand je navigue vers la page de la vente sans créneau
    Alors le bandeau affiche la plage de retrait sous "Récupère-la"

  @browser
  Scénario: Le tunnel de commande ne propose pas d'étape créneau
    Étant donné qu'une vente sans créneau existe avec des produits
    Et j'ai des produits dans mon panier de la vente sans créneau
    Quand je navigue vers le checkout de la vente sans créneau
    Alors le Stepper ne comporte que 3 étapes
    Et l'étape "Créneau" n'est pas affichée

  @browser
  Scénario: Commander sur une vente sans créneau
    Étant donné qu'une vente sans créneau existe avec des produits
    Et j'ai des produits dans mon panier de la vente sans créneau
    Quand je navigue vers le checkout de la vente sans créneau
    Et je remplis mes coordonnées et je continue
    Alors je vois le récapitulatif avec la plage de retrait
    Quand je confirme ma commande sans créneau
    Alors je suis redirigé vers la confirmation
    Et la confirmation affiche la plage de retrait

  Scénario: Une commande sans créneau est acceptée par l'API
    Étant donné qu'une vente sans créneau existe avec des produits
    Quand je poste une commande sans créneau
    Alors la commande est créée et n'a aucun créneau

  Scénario: Le créneau reste obligatoire sur une vente avec créneaux
    Étant donné qu'une vente existe avec des produits et des créneaux
    Quand je poste une commande sans créneau
    Alors la commande est refusée avec le statut 400

  Scénario: Changer le mode de retrait est refusé si la vente a des commandes
    Étant donné qu'une vente sans créneau existe avec des produits
    Et une commande a été passée sur la vente sans créneau
    Quand je bascule la vente sans créneau en mode créneaux
    Alors la modification est refusée avec le statut 409
