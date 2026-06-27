const token = localStorage.getItem("token");
const email = localStorage.getItem("email");
const API_URL = window.location.hostname === "localhost"
    ? "http://localhost:8080/api/v1"
    : "https://wallet-api-prod.onrender.com/api/v1";

if (!token) { window.location.href = "login.html"; }

// --- Funciones Principales ---

async function loadData() {
    try {
        // 1. Cargar Saldo
        const response = await fetch(`${API_URL}/wallet/saldo`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) {
            const err = new Error("Error en saldo");
            err.status = response.status;
            throw err;
        }
        const data = await response.json();
        document.getElementById("walletBalance").innerText = data.balance.toFixed(2);

        // 2. Cargar Transacciones
        const resTrans = await fetch(`${API_URL}/transactions/${email}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!resTrans.ok) throw new Error("Error al cargar transacciones");

        const transData = await resTrans.json();
        const tbody = document.getElementById("transactionsTableBody");
        tbody.innerHTML = "";

        transData.content.forEach(t => {
            tbody.innerHTML += `
            <tr class="transaction-row" data-id="${t.id}" style="cursor: pointer;">
                <td>${new Date(t.createdAt).toLocaleDateString()}</td>
                <td><strong>${t.type}</strong></td>
                <td>$${t.amount.toFixed(2)}</td>
                <td>${t.status}</td>
            </tr>
            `;
        });

        document.querySelectorAll('.transaction-row').forEach(row => {
            row.addEventListener('click', () => verDetalleTransaccion(row.getAttribute('data-id')));
        });
    } catch (err) {
        console.error("Error capturado:", err);
        if (err.status === 401 || err.status === 403) {
            alert("Tu sesión ha expirado.");
            localStorage.clear();
            window.location.href = "login.html";
        }
    }
}

async function verDetalleTransaccion(id) {
    try {
        const response = await fetch(`${API_URL}/transactions/${id}/${email}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error("No se pudo obtener el detalle");
        const t = await response.json();
        alert(`Detalle: ID: ${t.id} | Tipo: ${t.type} | Monto: $${t.amount.toFixed(2)} | Estado: ${t.status}`);
    } catch (err) { alert("No se pudo cargar el detalle."); }
}

// --- Eventos de Formularios ---

document.getElementById("depositForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById("depAmount").value);
    const response = await fetch(`${API_URL}/transactions/deposit`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount })
    });
    if (response.ok) { closeModal("depositModal"); loadData(); }
});

document.getElementById("transferForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const dest = document.getElementById("destEmail").value;
    const val = parseFloat(document.getElementById("transAmount").value);
    const response = await fetch(`${API_URL}/transactions/transfer`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ destinationEmail: dest, amount: val })
    });
    if (response.ok) { alert("Transferencia exitosa"); closeModal("transferModal"); loadData(); }
    else { alert("Error en la transferencia"); }
});

// --- Gestión de Perfil ---
document.getElementById("btnSettings").onclick = async () => {
    openModal("settingsModal");
    const response = await fetch(`${API_URL}/users/${email}`, {
        headers: { 'Authorization': `Bearer ${token}` }
    });
    if (response.ok) {
        const user = await response.json();
        document.getElementById("editFirstName").value = user.firstName || "";
        document.getElementById("editLastName").value = user.lastName || "";
    }
};

document.getElementById("profileForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const payload = { firstName: document.getElementById("editFirstName").value, lastName: document.getElementById("editLastName").value };
    const response = await fetch(`${API_URL}/users/update/${email}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (response.ok) { alert("Perfil actualizado"); closeModal("settingsModal"); }
});

document.getElementById("btnDeleteAccount").onclick = async () => {
    if (confirm("¿Eliminar cuenta?")) {
        const response = await fetch(`${API_URL}/users/deleted/${email}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
        if (response.ok) { localStorage.clear(); window.location.href = "login.html"; }
    }
};

// --- Integración Openpay ---
// Se inicializa al cargar la página
window.addEventListener('load', () => {
    if (typeof OpenPay !== 'undefined') {
        OpenPay.setId('tu_merchant_id');
        OpenPay.setApiKey('tu_public_key');
        OpenPay.setSandboxMode(true);
        const deviceSessionId = OpenPay.deviceData.setup("paymentForm", "device_session_id");

        document.getElementById("paymentForm").addEventListener("submit", (e) => {
            e.preventDefault();
            OpenPay.token.create({
                card_number: document.getElementById("card_number").value.replace(/\s/g, ''),
                holder_name: document.getElementById("card_holder").value,
                expiration_month: document.getElementById("exp_month").value,
                expiration_year: document.getElementById("exp_year").value,
                cvv2: document.getElementById("cvv").value
            }, async (response) => {
                const res = await fetch(`${API_URL}/transactions/pagos/cargo-tarjeta`, {
                    method: 'POST',
                    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        method: "card",
                        amount: 100.00,
                        source_id: response.data.id,
                        device_session_id: deviceSessionId
                    })
                });
                if (res.ok) { alert("Pago exitoso"); closeModal("paymentModal"); loadData(); }
            }, (error) => alert("Error en tarjeta: " + error.data.description));
        });
    }
});

// --- Utilidades ---
document.getElementById("btnLogout").onclick = () => { localStorage.clear(); window.location.href = "login.html"; };
function openModal(id) { document.getElementById(id).classList.remove("hidden"); }
function closeModal(id) { document.getElementById(id).classList.add("hidden"); }

document.getElementById("userEmailLabel").innerText = email;
loadData();