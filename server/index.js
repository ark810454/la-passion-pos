const http = require("http");
const { createHandler } = require("./core");

const PORT = Number(process.env.PORT || 4100);
const handler = createHandler();

const server = http.createServer(handler);

server.listen(PORT, () => {
  console.log(`La Passion API listening on http://localhost:${PORT}`);
});
