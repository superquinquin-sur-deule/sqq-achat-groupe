# Achat Groupé

A group purchasing platform for cooperatives. Organize collective purchasing events (ventes), let members browse products, place orders, and pick up and pay in store at scheduled time slots.

## Features

### For customers

- Browse active purchasing events (ventes)
- Product catalog with categories, images, and pricing (HT/TTC)
- Shopping cart with quantity management
- Multi-step checkout (customer info, time slot selection, order review)
- Payment in store at pickup — no online payment
- Order confirmation and tracking

### For administrators

- Dashboard with sales stats, top products, daily order charts, and time slot distribution
- Vente lifecycle management (create, activate, close, delete)
- Product management (CRUD, CSV bulk import, image upload)
- Time slot management with capacity control
- Order management (view, search, mark pickup, generate distribution PDF)
- Reception management (record supplier deliveries, handle shortages, adjust order amounts)
- Supplier order Excel export

## Tech Stack

- **Backend**: Quarkus (Java) with Clean Architecture / CQRS
- **Frontend**: Vue 3 + TypeScript, served via Quinoa
- **Database**: PostgreSQL with Flyway migrations
- **Auth**: Keycloak (OIDC) for admin authentication
- **Styling**: Tailwind CSS v4

## Getting Started

**Prerequisites**: Java 21+, Docker (for dev services)

```bash
./mvnw quarkus:dev
```

This starts everything — PostgreSQL and Keycloak are auto-provisioned via Quarkus Dev Services.

## Configuration

User-configurable environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `QUARKUS_OIDC_AUTH_SERVER_URL` | Keycloak realm URL | Auto-configured in dev |
| `QUARKUS_OIDC_CLIENT_ID` | OIDC client ID | Auto-configured in dev |
| `QUARKUS_OIDC_CREDENTIALS_SECRET` | OIDC client secret | Auto-configured in dev |
| `QUARKUS_DATASOURCE_JDBC_URL` | PostgreSQL JDBC URL | Auto-configured in dev |
| `QUARKUS_DATASOURCE_USERNAME` | Database username | Auto-configured in dev |
| `QUARKUS_DATASOURCE_PASSWORD` | Database password | Auto-configured in dev |

## Testing

```bash
./mvnw test
```

Runs Cucumber BDD and Playwright E2E tests.
