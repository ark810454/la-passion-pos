const fs = require("fs");
const path = require("path");
const { createPostgresStore } = require("../storage/postgres-store");

async function main() {
  if (!process.env.DATABASE_URL) {
    throw new Error("DATABASE_URL est obligatoire pour migrer les donnees vers Postgres.");
  }

  const snapshotPath = process.argv[2]
    ? path.resolve(process.cwd(), process.argv[2])
    : path.join(__dirname, "..", "data", "db.json");

  if (!fs.existsSync(snapshotPath)) {
    throw new Error(`Fichier de donnees introuvable: ${snapshotPath}`);
  }

  const snapshot = JSON.parse(fs.readFileSync(snapshotPath, "utf8"));
  const store = createPostgresStore();
  await store.init();
  await store.importSnapshot(snapshot);

  console.log(`Migration terminee vers Postgres depuis ${snapshotPath}`);
}

main().catch((error) => {
  console.error(error.message || error);
  process.exit(1);
});
