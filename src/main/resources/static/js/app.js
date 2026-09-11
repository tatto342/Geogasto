// ==============================================
// APP.JS - Consume el API de Spring Boot
// ==============================================

document.addEventListener('DOMContentLoaded', function () {
    cargarGastos();

    document.getElementById('form-gasto').addEventListener('submit', function (e) {
        e.preventDefault();
        alert('✅ En el Avance 1 solo mostramos los gastos de ejemplo. El registro se implementará en el Avance 2.');
    });
});

/**
 * Carga los gastos desde /api/gastos y los muestra en la lista.
 */
async function cargarGastos() {
    try {
        const response = await fetch('/api/gastos');
        if (!response.ok) throw new Error('Error al cargar gastos');

        const gastos = await response.json();
        renderizarLista(gastos);

    } catch (error) {
        console.error('Error:', error);
        document.getElementById('lista-gastos').innerHTML =
            '<li class="vacio">Error al cargar los gastos. ¿Está el backend encendido?</li>';
    }
}

/**
 * Renderiza la lista de gastos en el HTML.
 */
function renderizarLista(gastos) {
    const lista = document.getElementById('lista-gastos');

    if (!gastos || gastos.length === 0) {
        lista.innerHTML = '<li class="vacio">No hay gastos registrados.</li>';
        return;
    }

    lista.innerHTML = '';
    gastos.forEach(gasto => {
        const li = document.createElement('li');
        li.innerHTML = `
            <strong>${gasto.descripcion}</strong><br>
            S/ ${gasto.monto.toFixed(2)} • ${gasto.categoria}
        `;
        lista.appendChild(li);
    });
}