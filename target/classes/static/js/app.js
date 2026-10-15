
let mapaGasto;
let marcadorGasto;
let latitudGasto = null;
let longitudGasto = null;
let cambiandoUbicacion = false;

document.addEventListener('DOMContentLoaded', function () {
    cargarGastos();
    iniciarMapaGasto();

    const formulario = document.getElementById('form-gasto');

    formulario.addEventListener('submit', async function (e) {
        e.preventDefault();

        const descripcion = document.getElementById('descripcion').value.trim();
        const monto = Number(document.getElementById('monto').value);
        const categoria = document.getElementById('categoria').value;

        if (!descripcion || !Number.isFinite(monto) || monto <= 0 || !categoria) {
            alert('Completa todos los campos correctamente.');
            return;
        }

        if (latitudGasto === null || longitudGasto === null) {
            alert('Espera a que se detecte tu ubicación o selecciona un punto en el mapa.');
            return;
        }

        const gasto = {
            descripcion: descripcion,
            monto: monto,
            categoria: categoria,
            latitud: latitudGasto,
            longitud: longitudGasto
        };

        try {
            const respuesta = await fetch('/api/gastos', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(gasto)
            });

            if (!respuesta.ok) {
                const error = await respuesta.json().catch(() => ({}));
                throw new Error(error.error || 'No se pudo registrar el gasto.');
            }

            formulario.reset();
            await cargarGastos();
            alert('Gasto registrado correctamente.');

        } catch (error) {
            console.error('Error al registrar:', error);
            alert(error.message || 'No se pudo conectar con el servidor.');
        }
    });
});

function iniciarMapaGasto() {
    const estado = document.getElementById('estado-ubicacion');
    const botonCambiar = document.getElementById('btn-cambiar-ubicacion');

    if (!document.getElementById('mapa-gasto') || typeof L === 'undefined') {
        if (estado) {
            estado.textContent = 'No se pudo cargar el mapa.';
        }
        return;
    }

    // Vista inicial de Perú mientras se detecta la ubicación.
    mapaGasto = L.map('mapa-gasto').setView([-9.19, -75.0152], 5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(mapaGasto);

    marcadorGasto = L.marker([-9.19, -75.0152]).addTo(mapaGasto);
    marcadorGasto.bindPopup('Ubicación seleccionada');

    botonCambiar.addEventListener('click', function () {
        cambiandoUbicacion = !cambiandoUbicacion;

        botonCambiar.textContent = cambiandoUbicacion
            ? 'Cancelar cambio'
            : 'Cambiar ubicación';

        estado.textContent = cambiandoUbicacion
            ? 'Haz clic en el mapa para elegir la nueva ubicación.'
            : 'Se mantiene la ubicación seleccionada.';

        mapaGasto.getContainer().style.cursor =
            cambiandoUbicacion ? 'crosshair' : '';
    });

    mapaGasto.on('click', function (evento) {
        if (!cambiandoUbicacion) return;

        seleccionarUbicacion(
            evento.latlng.lat,
            evento.latlng.lng,
            'Ubicación seleccionada manualmente.'
        );

        cambiandoUbicacion = false;
        botonCambiar.textContent = 'Cambiar ubicación';
        mapaGasto.getContainer().style.cursor = '';
    });

    if (!navigator.geolocation) {
        estado.textContent =
            'Tu navegador no permite detectar la ubicación. Selecciona un punto en el mapa.';
        return;
    }

    estado.textContent = 'Solicitando permiso para detectar tu ubicación...';

    navigator.geolocation.getCurrentPosition(
        function (posicion) {
            seleccionarUbicacion(
                posicion.coords.latitude,
                posicion.coords.longitude,
                'Tu ubicación actual fue detectada.'
            );

            mapaGasto.setView(
                [latitudGasto, longitudGasto],
                16
            );
        },
        function (error) {
            console.warn('No se pudo detectar la ubicación:', error.message);

            estado.textContent =
                'No se detectó tu ubicación. Pulsa "Cambiar ubicación" y selecciona un punto del mapa.';
        },
        {
            enableHighAccuracy: true,
            timeout: 15000,
            maximumAge: 60000
        }
    );
}

function seleccionarUbicacion(latitud, longitud, mensaje) {
    latitudGasto = latitud;
    longitudGasto = longitud;

    marcadorGasto.setLatLng([latitud, longitud]);
    marcadorGasto.bindPopup('Ubicación seleccionada');

    document.getElementById('estado-ubicacion').textContent = mensaje;

    document.getElementById('coordenadas-ubicacion').textContent =
        `Latitud: ${latitud.toFixed(6)} | Longitud: ${longitud.toFixed(6)}`;
}

async function cargarGastos() {
    const lista = document.getElementById('lista-gastos');

    try {
        const respuesta = await fetch('/api/gastos');

        if (!respuesta.ok) {
            throw new Error('Error al cargar los gastos.');
        }

        const gastos = await respuesta.json();
        renderizarLista(gastos);

    } catch (error) {
        console.error('Error:', error);
        lista.textContent =
            'Error al cargar los gastos. ¿Está encendido el backend?';
    }
}

function renderizarLista(gastos) {
    const lista = document.getElementById('lista-gastos');
    lista.replaceChildren();

const total = (gastos || []).reduce((suma, gasto) => {
    return suma + Number(gasto.monto || 0);
}, 0);

document.getElementById('total-gastos').textContent =
    `S/ ${total.toLocaleString('es-PE', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    })}`;


    if (!gastos || gastos.length === 0) {
        const li = document.createElement('li');
        li.className = 'vacio';
        li.textContent = 'No hay gastos registrados.';
        lista.appendChild(li);
        return;
    }

    gastos.forEach(gasto => {
        const li = document.createElement('li');

        const descripcion = document.createElement('strong');
        descripcion.textContent = gasto.descripcion;

        const detalle = document.createElement('div');
        detalle.textContent =
            `S/ ${Number(gasto.monto).toFixed(2)} • ${gasto.categoria}`;

        const botonEliminar = document.createElement('button');
        botonEliminar.type = 'button';
        botonEliminar.textContent = 'Eliminar';
        botonEliminar.className = 'btn-primario';
        botonEliminar.addEventListener('click', () => eliminarGasto(gasto.id));

        li.appendChild(descripcion);
        li.appendChild(document.createElement('br'));
        li.appendChild(detalle);
        li.appendChild(botonEliminar);

        lista.appendChild(li);
    });
}

async function eliminarGasto(id) {
    if (!confirm('¿Seguro que deseas eliminar este gasto?')) {
        return;
    }

    try {
        const respuesta = await fetch(`/api/gastos/${id}`, {
            method: 'DELETE'
        });

        if (!respuesta.ok) {
            throw new Error('No se pudo eliminar el gasto.');
        }

        await cargarGastos();

    } catch (error) {
        console.error('Error al eliminar:', error);
        alert(error.message || 'Error al conectar con el servidor.');
    }
}
