# language: fr
Fonctionnalité: Smoke test
  Vérification que l'application démarre correctement

  Scénario: L'application est accessible
    Étant donné que l'application est démarrée
    Quand j'accède au health check
    Alors le statut est "UP"
