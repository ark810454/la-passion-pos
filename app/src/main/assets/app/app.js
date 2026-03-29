const INTERNAL_PRINTER = "INTERNAL:CS10";

function ensureBridge() {
  if (!window.AndroidPos) {
    throw new Error("Bridge Android indisponible.");
  }
  return window.AndroidPos;
}

function buildPayload() {
  const message = document
    .getElementById("messageInput")
    .value.split("\n")
    .map((line) => line.trim())
    .filter(Boolean);

  return {
    storeName: "",
    storeAddress: "",
    storePhone: "",
    ticketNumber: "",
    date: "",
    total: "",
    headerLines: [],
    footerLines: message,
    items: [],
    qrData: "",
  };
}

async function printTicket() {
  try {
    const bridge = ensureBridge();
    const payload = buildPayload();
    if (!payload.footerLines.length) {
      throw new Error("Saisissez un message.");
    }
    bridge.printTicket(INTERNAL_PRINTER, JSON.stringify(payload));
    alert("Impression envoyee.");
  } catch (error) {
    alert(error.message);
  }
}

function loadSample() {
  document.getElementById("messageInput").value = [
    "Bonjour",
    "Impression de test",
    "Merci",
  ].join("\n");
}

document.getElementById("printBtn").addEventListener("click", printTicket);
document.getElementById("sampleBtn").addEventListener("click", loadSample);

loadSample();
