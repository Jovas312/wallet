const token = localStorage.getItem("token");
const email = localStorage.getItem("email");
const API_URL = window.location.hostname === "localhost"
? "http://localhost:8080/api/v1"
: "https://wallet-api-prod.onrender.com/api/v1";


if (!token) { window.location.href = "login.html"; }

document.getElementById("userEmailLabel").innerText = email;

async function loadData() {
    try {
        const response = await fetch(`${API_URL}/wallet/saldo`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if(!response.ok) throw new Error("Sesión expirada");

        const data = await response.json();
        document.getElementById("walletBalance").innerText = data.saldo.toFixed(2);

        const resTrans = await fetch(`${API_URL}/transactions/${email}`, {
        headers: { 'Authorization': `Bearer ${token}` }
        });

        const transData = await resTrans.json();
        const tbody = document.getElementById("transactionsTableBody");
        tbody.innerHTML = "";

        transData.content.forEach(t => {
            tbody.innerHTML += `
            <tr>
                <td>${new Date(t.createdAt).toLocaleDateString()}</td>
                <td><strong>${t.type}</strong></td>
                <td>$${t.amount.toFixed(2)}</td>
                <td>${t.status}</td>
            </tr>
            `;
        });
    } catch (err){
        console.error("Error cargando datos", err);
        localStorage.clear();
        window.location.href = "login.html";
    }
}

document.getElementById("depositForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById("depAmount").value);

    await fetch(`${API_URL}/transactions/deposit`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ amount: amount })
    });
    
    closeModal("depositModal");
    loadData();
});

document.getElementById("btnLogout").onclick = () => { localStorage.clear(); window.location.href = "login.html"; };
function openModal(id) { document.getElementById(id).classList.remove("hidden"); }
function closeModal(id) { document.getElementById(id).classList.add("hidden"); }

loadData();