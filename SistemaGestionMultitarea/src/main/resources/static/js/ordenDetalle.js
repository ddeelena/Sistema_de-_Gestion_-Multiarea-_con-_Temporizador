const API_BASE = "http://localhost:8080/api/ordenes";
const ordenId = new URLSearchParams(window.location.search).get("id") || "1"; // ?id=xxxx

document.addEventListener("DOMContentLoaded", async () => {
    await cargarOrden();
    await cargarHistorial();
    document.getElementById("btnRefrescar").addEventListener("click", async () => {
        await cargarOrden();
        await cargarHistorial();
    });
});

async function cargarOrden() {
    const resp = await fetch(`${API_BASE}/${ordenId}`);
    if (!resp.ok) return alert("Error al obtener la orden");
    const orden = await resp.json();

    document.getElementById("ordenId").textContent = orden.id;
    document.getElementById("descripcion").textContent = orden.descripcion || "(sin descripción)";
    document.getElementById("estadoGlobal").textContent = orden.estadoGlobal;

    const tbody = document.getElementById("tablaAreas");
    tbody.innerHTML = "";

    // Si el backend expone las áreas relacionadas (puedes adaptarlo según tu modelo)
    const areas = orden.areas || [
        { id: "a1", nombre: "Producción", estado: "PENDIENTE", seg: 0 },
        { id: "a2", nombre: "Calidad", estado: "PENDIENTE", seg: 0 },
    ];

    areas.forEach(a => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${a.nombre}</td>
      <td>${a.estado}</td>
      <td>${a.seg ?? 0}</td>
      <td>
        <button class="iniciar" onclick="accionArea('${a.id}','INICIAR')">Iniciar</button>
        <button class="pausar" onclick="accionArea('${a.id}','PAUSAR')">Pausar</button>
        <button class="completar" onclick="accionArea('${a.id}','COMPLETAR')">Completar</button>
        <button class="sin-solucion" onclick="accionArea('${a.id}','SIN_SOLUCION')">Sin solución</button>
      </td>
    `;
        tbody.appendChild(tr);
    });
}

async function accionArea(areaId, accion) {
    try {
        let nuevoEstado = "";
        switch (accion) {
            case "INICIAR": nuevoEstado = "EN_PROCESO"; break;
            case "PAUSAR": nuevoEstado = "PAUSADA"; break;
            case "COMPLETAR": nuevoEstado = "COMPLETADA"; break;
            case "SIN_SOLUCION": nuevoEstado = "SIN_SOLUCION"; break;
        }

        const resp = await fetch(`${API_BASE}/${ordenId}/estado?nuevoEstado=${nuevoEstado}`, {
            method: "PUT"
        });

        if (!resp.ok) throw new Error("Error al cambiar estado");
        await cargarOrden();
        await cargarHistorial();
    } catch (err) {
        alert(err.message);
    }
}

async function cargarHistorial() {
    const resp = await fetch(`${API_BASE}/${ordenId}/historial`);
    if (!resp.ok) return alert("Error al cargar historial");
    const data = await resp.json();
    const lista = document.getElementById("listaHistorial");
    lista.innerHTML = "";
    data.forEach(h => {
        const li = document.createElement("li");
        li.textContent = `${new Date(h.timestamp).toLocaleString()} — ${h.evento}: ${h.detalle} (${h.estadoGlobal})`;
        lista.appendChild(li);
    });
}
