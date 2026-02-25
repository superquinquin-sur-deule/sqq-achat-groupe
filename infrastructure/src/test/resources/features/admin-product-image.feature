# language: fr
@browser @admin-product-image
Fonctionnalité: Upload d'image pour les produits
  En tant qu'administrateur, je veux pouvoir associer une image à un produit
  afin que les coopérateurs voient les produits illustrés dans le catalogue.

  Contexte:
    Étant donné qu'une vente existe avec des produits et des créneaux

  Scénario: Uploader une image via l'API et la récupérer
    Quand j'uploade une image PNG sur le premier produit via l'API
    Alors l'image est accessible via l'endpoint public
    Et la réponse produit contient l'URL de l'image

  Scénario: L'image uploadée s'affiche dans le catalogue
    Quand j'uploade une image PNG sur le premier produit via l'API
    Et j'accède à la page du catalogue
    Alors la carte du produit affiche l'image
