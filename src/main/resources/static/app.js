const API_URL = window.location.hostname === "localhost"
? "http://localhost:8080/api/v1"
: "https://wallet-api-prod.onrender.com/api/v1";

// Redirigir automáticamente si el usuario ya tiene sesión activa
if (localStorage.getItem("token")) {
    window.location.href = "dashboard.html";
}

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({ email, password})
        });
        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token);
            localStorage.setItem("email", email);
            window.location.href = "dashboard.html";
        } else {
            alert("Error: Usuario o contraseña incorrectos.")
        }
    } catch (error) {
        console.error("Error de conexion:", error);
    }
});