const API_URL = "http://localhost:8080/api/v1";

document.getElementById("registerForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const email = document.getElementById("regEmail").value;

    alert("¡Cuenta registrada con éxito (Simulación)!");
    
    // SIMULACIÓN: Iniciamos sesión automáticamente con el correo registrado
    localStorage.setItem("token", "token-de-prueba-sin-backend-12345");
    localStorage.setItem("email", email);
    
    window.location.href = "dashboard.html";
});