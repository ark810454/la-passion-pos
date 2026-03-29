const { createFileStore } = require("./file-store");

function createStorage() {
  if (getDatabaseUrl()) {
    const { createPostgresStore } = require("./postgres-store");
    return createPostgresStore();
  }
  return createFileStore();
}

function getDatabaseUrl() {
  return process.env.DATABASE_URL || process.env.POSTGRES_URL || process.env.POSTGRES_PRISMA_URL || "";
}

module.exports = {
  createStorage,
};
