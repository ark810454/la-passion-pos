const { createFileStore } = require("./file-store");

function createStorage() {
  if (process.env.DATABASE_URL) {
    const { createPostgresStore } = require("./postgres-store");
    return createPostgresStore();
  }
  return createFileStore();
}

module.exports = {
  createStorage,
};
