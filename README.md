# Achat Groupé

A group purchasing platform for cooperatives. Organize collective purchasing events (ventes), let members browse products, place orders, pay via Stripe, and pick up at scheduled time slots.

## Features

### For customers

- Browse active purchasing events (ventes)
- Product catalog with categories, images, and pricing (HT/TTC)
- Shopping cart with quantity management
- Multi-step checkout (customer info, time slot selection, order review)
- Stripe payment with retry support
- Order confirmation and tracking

### For administrators

- Dashboard with sales stats, top products, daily order charts, and time slot distribution
- Vente lifecycle management (create, activate, close, delete)
- Product management (CRUD, CSV bulk import, image upload)
- Time slot management with capacity control
- Order management (view, search, mark pickup, generate distribution PDF)
- Reception management (record supplier deliveries, handle shortages, process refunds)
- Supplier order Excel export

## Tech Stack

- **Backend**: Quarkus (Java) with Clean Architecture / CQRS
- **Frontend**: Vue 3 + TypeScript, served via Quinoa
- **Database**: PostgreSQL with Flyway migrations
- **Auth**: Keycloak (OIDC) for admin authentication
- **Payments**: Stripe
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
| `APP_BASE_URL` | Application base URL (used for Stripe redirects) | `http://localhost:8080` |
| `STRIPE_API_KEY` | Stripe secret API key | `sk_test_dummy` |
| `STRIPE_WEBHOOK_SECRET` | Stripe webhook signing secret | `dummy` |
| `QUARKUS_OIDC_AUTH_SERVER_URL` | Keycloak realm URL | Auto-configured in dev |
| `QUARKUS_OIDC_CLIENT_ID` | OIDC client ID | Auto-configured in dev |
| `QUARKUS_OIDC_CREDENTIALS_SECRET` | OIDC client secret | Auto-configured in dev |
| `QUARKUS_DATASOURCE_JDBC_URL` | PostgreSQL JDBC URL | Auto-configured in dev |
| `QUARKUS_DATASOURCE_USERNAME` | Database username | Auto-configured in dev |
| `QUARKUS_DATASOURCE_PASSWORD` | Database password | Auto-configured in dev |

Optional tuning:

| Variable | Description | Default |
|----------|-------------|---------|
| `APP_ORDER_MAX_PAYMENT_ATTEMPTS` | Maximum payment retry attempts | `2` |
| `APP_ORDER_ABANDON_TIMEOUT` | Timeout before unpaid orders are abandoned | `30m` |

## Testing

```bash
./mvnw test
```

Runs Cucumber BDD and Playwright E2E tests.
