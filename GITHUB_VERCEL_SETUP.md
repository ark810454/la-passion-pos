# Publication GitHub + Vercel

## 1. Initialiser le depot local

```bat
git init
git add .
git commit -m "Initial La Passion release"
```

## 2. Creer le depot GitHub

Creer un nouveau depot vide sur GitHub, par exemple:

```text
la-passion-pos
```

Puis relier le projet local:

```bat
git remote add origin https://github.com/VOTRE-COMPTE/la-passion-pos.git
git branch -M main
git push -u origin main
```

## 3. Importer dans Vercel

1. Ouvrir Vercel.
2. Cliquer sur `Add New Project`.
3. Choisir le depot GitHub `la-passion-pos`.
4. Laisser Vercel detecter automatiquement le projet.

## 4. Ajouter la base distante

Creer une base Postgres distante:

- Vercel Postgres
- Neon
- Supabase

Copier ensuite la variable:

```text
DATABASE_URL
```

## 5. Configurer les variables Vercel

Ajouter dans le projet Vercel:

```text
DATABASE_URL=postgres://...
APP_ENV=production
```

## 6. Migrer les donnees locales existantes

Si vous avez deja des produits ou commandes en local:

```bat
set DATABASE_URL=postgres://user:password@host:5432/la_passion
node server\scripts\seed-postgres.js
```

## 7. Deployer

Lancer le deploiement depuis Vercel. Une fois termine:

- admin PC: `https://votre-projet.vercel.app/admin`
- API santé: `https://votre-projet.vercel.app/api/health`

## 8. Connecter le POS

Dans `La Passion` sur le terminal:

1. Saisir l'URL publique Vercel.
2. Appuyer sur `Enregistrer`.
3. Appuyer sur `Sync catalogue`.

## 9. Verification rapide

- `/api/health` repond avec `storage: "postgres"`
- les produits modifies sur le PC admin apparaissent sur le POS
- une commande imprimee remonte dans le dashboard admin
