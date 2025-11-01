//const API_URL = "/ordenes"; // endpoint del backend Spring Boot
const API_URL = "http://localhost:8080/api/ordenes"

document.addEventListener("DOMContentLoaded", () => {
    const selectEstado = document.getElementById("estado");
    selectEstado.addEventListener("change", loadOrdenes);
    loadOrdenes();
});

async function loadOrdenes() {
    const estado = document.getElementById("estado").value;
    let url = API_URL;
    if (estado) url += `?estado=${estado}`;

    try {
        const res = await fetch(url);
        if (!res.ok) throw new Error("Error al obtener las órdenes");

        const data = await res.json();
        renderTable(data);
        renderKPIs(data);
    } catch (err) {
        console.error(err);
        alert("Error cargando las órdenes");
    }
}

function renderTable(ordenes) {
    const tbody = document.getElementById("ordenes-body");
    tbody.innerHTML = "";

    ordenes.forEach(o => {
        const row = document.createElement("tr");
        row.innerHTML = `
      <td>${o.id}</td>
      <td>${o.titulo}</td>
      <td>${o.estadoGlobal}</td>
      <td>${o.creador}</td>
      <td>${new Date(o.actualizadaEn || o.creadaEn).toLocaleString()}</td>
    `;
        tbody.appendChild(row);
    });
}

function renderKPIs(ordenes) {
    const total = ordenes.length;
    const completadas = ordenes.filter(o => o.estadoGlobal === "COMPLETADA").length;
    const pendientes = ordenes.filter(o => o.estadoGlobal !== "COMPLETADA" && o.estadoGlobal !== "CERRADA_SIN_SOLUCION").length;
    const sinSolucion = ordenes.filter(o => o.estadoGlobal === "CERRADA_SIN_SOLUCION").length;

    document.getElementById("total").innerText = total;
    document.getElementById("completadas").innerText = completadas;
    document.getElementById("pendientes").innerText = pendientes;
    document.getElementById("sin-solucion").innerText = sinSolucion;
}
