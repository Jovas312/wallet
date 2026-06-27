const token = localStorage.getItem("token");
const email = localStorage.getItem("email");
const API_URL = window.location.hostname === "localhost"
    ? "http://localhost:8080/api/v1"
    : "https://wallet-api-prod.onrender.com/api/v1";

if (!token) { window.location.href = "login.html"; }

// --- 1. Inicialización de eventos al cargar el DOM ---
document.addEventListener("DOMContentLoaded", () => {
    // Configuración de Perfil
    document.getElementById("btnSettings").addEventListener("click", abrirConfiguracion);
    document.getElementById("btnDeleteAccount").addEventListener("click", eliminarCuenta);
    document.getElementById("btnLogout").addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "login.html";
    });

    // Eventos de Formularios
    document.getElementById("profileForm").addEventListener("submit", guardarPerfil);
    document.getElementById("depositForm").addEventListener("submit", realizarDeposito);
    document.getElementById("transferForm").addEventListener("submit", realizarTransferencia);

    // Carga inicial
    document.getElementById("userEmailLabel").innerText = email;
    loadData();
    inicializarOpenpay();
});

// --- 2. Lógica de Perfil y cuenta ---
async function abrirConfiguracion() {
    openModal("settingsModal");
    try {
        const res = await fetch(`${API_URL}/users/${email}`, { headers: { 'Authorization': `Bearer ${token}` } });
        if (res.ok) {
            const user = await res.json();
            document.getElementById("editFirstName").value = user.firstName || "";
            document.getElementById("editLastName").value = user.lastName || "";
        }
    } catch (err) { console.error("Error al cargar perfil:", err); }
}

async function guardarPerfil(e) {
    e.preventDefault();
    const payload = {
        firstName: document.getElementById("editFirstName").value,
        lastName: document.getElementById("editLastName").value
    };
    const res = await fetch(`${API_URL}/users/update/${email}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) { alert("Perfil actualizado"); closeModal("settingsModal"); }
}

async function eliminarCuenta() {
    if (confirm("¿Eliminar cuenta permanentemente?")) {
        const res = await fetch(`${API_URL}/users/deleted/${email}`, { method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` } });
        if (res.ok) { localStorage.clear(); window.location.href = "login.html"; }
    }
}

// --- 3. Lógica de Datos y Transacciones ---
async function loadData() {
    try {
        const resSaldo = await fetch(`${API_URL}/wallet/saldo`, { headers: { 'Authorization': `Bearer ${token}` } });
        const resTrans = await fetch(`${API_URL}/transactions/${email}`, { headers: { 'Authorization': `Bearer ${token}` } });

        if (resSaldo.ok) {
            const data = await resSaldo.json();
            document.getElementById("walletBalance").innerText = data.balance.toFixed(2);
        }

        if (resTrans.ok) {
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
                </tr>`;
            });
            document.querySelectorAll('.transaction-row').forEach(row => {
                row.addEventListener('click', () => verDetalleTransaccion(row.getAttribute('data-id')));
            });

            // Y asegúrate de tener definida esta función:
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
        }
    } catch (err) { console.error("Error al cargar datos:", err); }
}

async function realizarDeposito(e) {
    e.preventDefault();
    await fetch(`${API_URL}/transactions/deposit`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ amount: parseFloat(document.getElementById("depAmount").value) })
    });
    closeModal("depositModal"); loadData();
}

async function realizarTransferencia(e) {
    e.preventDefault();
    await fetch(`${API_URL}/transactions/transfer`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({
            destinationEmail: document.getElementById("destEmail").value,
            amount: parseFloat(document.getElementById("transAmount").value)
        })
    });
    alert("Transferencia exitosa"); closeModal("transferModal"); loadData();
}

// --- 4. Openpay y Utilerías ---
function inicializarOpenpay() {
    if (typeof OpenPay !== 'undefined') {
        OpenPay.setId('mxipm8p3keuc9u4fpxzl');
        OpenPay.setApiKey('sk_566e53d64c194ccc92684dc3a59d3141');
        OpenPay.setSandboxMode(true);
        const deviceSessionId = OpenPay.deviceData.setup("paymentForm", "device_session_id");
        document.getElementById("device_session_id").value = deviceSessionId;

        document.getElementById("paymentForm").addEventListener("submit", (e) => {
            e.preventDefault();
            OpenPay.token.create({
                card_number: document.getElementById("card_number").value.replace(/\s/g, ''),
                holder_name: document.getElementById("card_holder").value,
                expiration_month: document.getElementById("exp_month").value,
                expiration_year: document.getElementById("exp_year").value,
                cvv2: document.getElementById("cvv").value
            }, async (res) => {
                const cargo = await fetch(`${API_URL}/transactions/pagos/cargo-tarjeta`, {
                    method: 'POST',
                    headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        method: "card",
                        amount: 100.00, // Ajusta según tu lógica
                        source_id: res.data.id,
                        device_session_id: deviceSessionId
                    })
                });
                if (cargo.ok) { alert("Pago exitoso"); closeModal("paymentModal"); loadData(); }
            }, (err) => alert("Error Openpay: " + err.data.description));
        });
    }
}

function openModal(id) { document.getElementById(id).classList.remove("hidden"); }
function closeModal(id) { document.getElementById(id).classList.add("hidden"); }