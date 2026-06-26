const API_URL = "http://localhost:8080/api/v1";

// Redirigir automáticamente si el usuario ya tiene sesión activa
if (localStorage.getItem("token")) {
    window.location.href = "dashboard.html";
}

document.getElementById("loginForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const email = document.getElementById("loginEmail").value;

    // SIMULACIÓN: Guardamos datos ficticios en la sesión local
    localStorage.setItem("token", "token-de-prueba-sin-backend-12345");
    localStorage.setItem("email", email);
    
    // Redirigir de inmediato al Dashboard simulado
    window.location.href = "dashboard.html";
});