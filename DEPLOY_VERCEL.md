# Deploiement Vercel

Le projet est maintenant prepare pour Vercel avec:

- `api/index.js` pour l'API
- `public/admin/` pour le dashboard PC
- `vercel.json` pour les routes
- `server/storage/postgres-store.js` pour une vraie base distante

## Ce qui marche deja

- `GET` sur les endpoints API
- dashboard admin statique
- structure compatible Vercel
- bascule automatique sur Postgres si `DATABASE_URL` est definie

## Limite importante

Sans `DATABASE_URL`, le projet retombe sur `server/data/db.json` en local.

Sur Vercel, ce stockage fichier n'est pas adapte a la production. Les endpoints d'ecriture renverront une erreur explicite tant qu'une base distante n'est pas branchee.

## Prochaine etape obligatoire pour la prod

Remplacer `server/data/db.json` par une vraie base distante, par exemple:

- Vercel Postgres
- Neon Postgres
- Supabase Postgres

## URL cible

- Admin: `/admin`
- API: `/api/health`, `/api/bootstrap`, etc.

## Variables conseillees pour la prod

- `DATABASE_URL`
- `APP_ENV=production`
- `ALLOW_FILE_DB_ON_VERCEL` uniquement pour des tests tres limites, pas pour la production

## Fonctionnement du stockage

- `DATABASE_URL` definie: API dynamique avec Postgres
- `DATABASE_URL` absente en local: fallback sur `server/data/db.json`
- `DATABASE_URL` absente sur Vercel: lecture possible, ecritures bloquees avec message explicite

## Schema SQL

Le schema de base est fourni dans:

```text
server/storage/schema.sql
```

## Test local

```bat
node server\index.js
```

Puis ouvrir:

```text
http://localhost:4100/admin/
```

## Mise en production conseillee

1. Creer une base Postgres distante sur Vercel Postgres, Neon ou Supabase.
2. Renseigner `DATABASE_URL` dans Vercel.
3. Si vous avez deja des donnees locales, lancer la migration:

```bat
set DATABASE_URL=postgres://user:password@host:5432/la_passion
node server\scripts\seed-postgres.js
```

4. Deployer le projet.
5. Ouvrir `/admin` sur l'URL publique Vercel.
6. Dans le POS, remplacer l'URL locale par l'URL publique Vercel.
