# La Passion

Solution POS restaurant et terrasse composee de:

- une application Android POS pour le terminal client
- une API centrale Node.js pour la synchronisation multi-appareils
- un dashboard web admin pour PC

## Architecture

- `app/`: application Android POS avec impression integree
- `server/index.js`: API HTTP centrale pour le mode local
- `server/storage/`: adaptateurs de stockage `file` et `postgres`
- `server/data/db.json`: base JSON locale de secours
- `server/admin/`: dashboard web administrateur pour PC
- `public/admin/`: version statique du dashboard pour Vercel
- `api/index.js`: entree API pour Vercel
- `run-la-passion-server.cmd`: lancement rapide du serveur

## Fonctionnalites actuelles

### POS Android

- modules `Restaurant` et `Terrasse`
- gestion des tables
- catalogue rapide
- saisie manuelle produit
- happy hour terrasse
- paiement `cash`, `mobile money`, `carte`
- impression locale sur POS CS10
- synchronisation du catalogue depuis le PC admin
- envoi des commandes payees vers le serveur

### Admin Web PC

- modification des produits
- suppression des produits
- modification du statut des tables
- parametres de l'etablissement
- statistiques consolidees
- liste des commandes synchronisees

## Lancer le serveur PC

Depuis le dossier projet:

```bat
run-la-passion-server.cmd
```

ou:

```bat
node server\index.js
```

Le dashboard admin est ensuite disponible sur:

```text
http://localhost:4100/admin/
```

## Mode Vercel

Le projet peut maintenant tourner en deux modes:

- local: `node server\\index.js` avec `server/data/db.json`
- production: Vercel + `DATABASE_URL` Postgres

Un exemple de variables est fourni dans:

```text
.env.example
```

Le guide de publication GitHub + Vercel est dans:

```text
GITHUB_VERCEL_SETUP.md
```

Quand `DATABASE_URL` est definie, l'API utilise automatiquement Postgres pour que le POS, le PC admin et les autres appareils partagent les memes donnees.

## Connecter le POS au PC admin

1. Lancer le serveur sur le PC.
2. Relever l'IP locale du PC.
3. Dans le POS, saisir l'URL serveur, par exemple:

```text
http://192.168.1.10:4100
```

4. Appuyer sur `Enregistrer`.
5. Appuyer sur `Sync catalogue`.

## Connecter le POS a Vercel

1. Deployer l'API et l'admin sur Vercel.
2. Configurer `DATABASE_URL`.
3. Recuperer l'URL publique, par exemple:

```text
https://la-passion.vercel.app
```

4. Dans le POS, saisir cette URL.
5. Appuyer sur `Enregistrer`, puis `Sync catalogue`.

## Construire l'APK Android

```bat
gradle assembleDebug
```

Ou ouvrir le projet dans Android Studio puis lancer l'application sur le POS.

## Roadmap recommandee

- comptes utilisateurs avec PIN
- historique et cloture de caisse
- notifications temps reel entre appareils
- mode hors ligne robuste avec reprise de synchronisation
- authentification admin securisee
- notifications cuisine en temps reel
