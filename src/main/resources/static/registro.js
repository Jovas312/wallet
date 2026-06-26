const API_URL = window.location.hostname === "localhost"
? "http://localhost:8080/api/v1"
: "https://wallet-api-prod.onrender.com/api/v1";

document.getElementById("registerForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const user = {
        firstName: document.getElementById("regFirstName").value,
        lastName: document.getElementById("regLastName").value,
        email: document.getElementById("regEmail").value,
        password: document.getElementById("regPassword").value,
        documentId: document.getElementById("regDocumentId").value
    };

    const res = await fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(user)
    });
    
    if (res.ok) {
        alert("Registro exitoso");
        window.location.href = "login.html";
    } else {
        alert("Error al registrar");
    }
});