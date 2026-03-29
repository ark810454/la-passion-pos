const kitchenApi = {
  async get(path) {
    const response = await fetch(path);
    if (!response.ok) throw new Error(await response.text());
    return response.json();
  },
  async put(path, body) {
    const response = await fetch(path, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!response.ok) throw new Error(await response.text());
    return response.json();
  },
};

async function loadKitchen() {
  try {
    const orders = await kitchenApi.get("/api/orders");
    const restaurantOrders = orders.filter((order) => order.source === "restaurant");
    renderKitchenStats(restaurantOrders);
    renderKitchenOrders(restaurantOrders);
    setKitchenStatus("Cuisine connectee");
  } catch (error) {
    setKitchenStatus(`Erreur: ${error.message}`);
  }
}

function renderKitchenStats(orders) {
  const pending = orders.filter((order) => order.status === "pending").length;
  const preparing = orders.filter((order) => order.status === "preparing").length;
  const ready = orders.filter((order) => order.status === "ready").length;
  const cards = [
    ["En attente", pending],
    ["En preparation", preparing],
    ["Pretes", ready],
    ["Total cuisine", orders.length],
  ];
  document.getElementById("kitchenStatsCards").innerHTML = cards
    .map(([label, value]) => `<div class="card"><strong>${label}</strong><span>${value}</span></div>`)
    .join("");
}

function renderKitchenOrders(orders) {
  const container = document.getElementById("kitchenOrders");
  if (!orders.length) {
    container.innerHTML = `<div class="order-card">Aucune commande cuisine.</div>`;
    return;
  }

  container.innerHTML = orders
    .map(
      (order) => `
        <div class="order-card">
          <div class="panel-head">
            <strong>${order.tableId || "Salle"}</strong>
            <span class="pill">${order.status || "pending"}</span>
          </div>
          <div>Date: ${order.printedAt || order.createdAt}</div>
          <div style="margin-top:8px">${(order.items || [])
            .map((item) => `${item.quantity} x ${item.name}`)
            .join("<br/>")}</div>
          <div style="display:flex;gap:8px;margin-top:14px">
            <button data-order-id="${order.id}" data-status="preparing">En preparation</button>
            <button data-order-id="${order.id}" data-status="ready">Prete</button>
            <button data-order-id="${order.id}" data-status="served">Servie</button>
          </div>
        </div>
      `
    )
    .join("");

  document.querySelectorAll("[data-order-id]").forEach((button) => {
    button.addEventListener("click", async () => {
      await kitchenApi.put(`/api/orders/${button.dataset.orderId}`, {
        status: button.dataset.status,
      });
      loadKitchen();
    });
  });
}

function setKitchenStatus(text) {
  document.getElementById("kitchenStatusBadge").textContent = text;
}

document.getElementById("refreshKitchenBtn").addEventListener("click", loadKitchen);
loadKitchen();
setInterval(loadKitchen, 5000);
