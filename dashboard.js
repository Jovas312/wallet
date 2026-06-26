 const token = localStorage.getItem("token");
const email = localStorage.getItem("email");


if (!token) {
    window.location.href = "login.html";
}

document.getElementById("userEmailLabel").innerText = email;


let saldoActual = 1500.00;


let transaccionesSimuladas = [
    { createdAt: new Date().toISOString(), type: 'DEPOSITO', sourceUserEmail: email, destinationUserEmail: email, amount: 500.00, status: 'COMPLETADO' },
    { createdAt: new Date(Date.now() - 86400000).toISOString(), type: 'TRANSFERENCIA', sourceUserEmail: email, destinationUserEmail: 'maria@email.com', amount: 200.00, status: 'COMPLETADO' },
    { createdAt: new Date(Date.now() - 172800000).toISOString(), type: 'TRANSFERENCIA', sourceUserEmail: 'carlos@email.com', destinationUserEmail: email, amount: 350.00, status: 'COMPLETADO' }
];

function loadData() {
    
    document.getElementById("walletBalance").innerText = saldoActual.toFixed(2);


    const tbody = document.getElementById("transactionsTableBody");
    tbody.innerHTML = "";

    transaccionesSimuladas.forEach(t => {
        const isOut = t.sourceUserEmail === email && t.type === 'TRANSFER';
        tbody.innerHTML += `
            <tr>
                <td>${new Date(t.createdAt).toLocaleDateString()}</td>
                <td><strong>${t.type}</strong><br><small style="color:gray">${isOut ? 'A: '+t.destinationUserEmail : 'De: '+t.sourceUserEmail}</small></td>
                <td style="color: ${isOut ? '#dc2626' : '#10b981'}; font-weight: bold;">
                    ${isOut ? '-' : '+'}$${t.amount.toFixed(2)}
                </td>
                <td><span style="background:#e5e7eb; padding:2px 6px; border-radius:4px; font-size:0.85rem;">${t.status}</span></td>
            </tr>
        `;
    });
}


document.getElementById("depositForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const amount = parseFloat(document.getElementById("depAmount").value);
    
   
    saldoActual += amount;
    transaccionesSimuladas.unshift({
        createdAt: new Date().toISOString(),
        type: 'DEPOSIT',
        sourceUserEmail: email,
        destinationUserEmail: email,
        amount: amount,
        status: 'COMPLETED'
    });

    closeModal("depositModal");
    loadData(); 
});

document.getElementById("transferForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const destEmail = document.getElementById("transEmail").value;
    const amount = parseFloat(document.getElementById("transAmount").value);
    
    if (amount > saldoActual) {
        alert("Saldo insuficiente para realizar esta transferencia ficticia.");
        return;
    }


    saldoActual -= amount;
    transaccionesSimuladas.unshift({
        createdAt: new Date().toISOString(),
        type: 'TRANSFER',
        sourceUserEmail: email,
        destinationUserEmail: destEmail,
        amount: amount,
        status: 'COMPLETED'
    });

    closeModal("transferModal");
    alert("¡Transferencia simulada con éxito!");
    loadData();
});


document.getElementById("btnLogout").onclick = () => {
    localStorage.clear();
    window.location.href = "login.html";
};

function openModal(id) { document.getElementById(id).classList.remove("hidden"); }
function closeModal(id) { document.getElementById(id).classList.add("hidden"); }

loadData();