// =====================================================================
// sesion.js - Funciones compartidas por todas las pantallas (EVA 02)
//  - api(): llama al backend y muestra los errores que devuelve
//  - iniciarSesion(): obtiene el usuario autenticado, arma el menú
//    según su rol y oculta los elementos que no le corresponden
// =====================================================================

const API_URL = '/api';
let USUARIO_ACTUAL = null;

// Opciones del menú superior y los roles que pueden verlas
const MENU = [
    { url: '/index.html',     icono: 'bi-clipboard2-pulse', texto: 'Panel Clínico', roles: ['ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA'] },
    { url: '/usuarios.html',  icono: 'bi-people',           texto: 'Usuarios',      roles: ['ADMINISTRADOR'] },
    { url: '/roles.html',     icono: 'bi-shield-lock',      texto: 'Roles',         roles: ['ADMINISTRADOR'] },
    { url: '/auditoria.html', icono: 'bi-journal-text',     texto: 'Auditoría',     roles: ['ADMINISTRADOR'] }
];

async function api(url, opciones = {}) {
    const res = await fetch(API_URL + url, {
        headers: { 'Content-Type': 'application/json' },
        ...opciones
    });
    if (res.status === 401) {
        window.location.href = '/login.html';
        throw new Error('Su sesión ha expirado.');
    }
    const texto = await res.text();
    let data = null;
    if (texto) {
        try { data = JSON.parse(texto); } catch (e) { data = null; }
    }
    if (!res.ok) {
        throw new Error((data && data.mensaje) || `Error ${res.status} al comunicarse con el servidor.`);
    }
    return data;
}

async function iniciarSesion() {
    USUARIO_ACTUAL = await api('/auth/me');
    const rol = USUARIO_ACTUAL.rolNombre;

    // Menú: solo las opciones permitidas para el rol
    const paginaActual = window.location.pathname === '/' ? '/index.html' : window.location.pathname;
    const opciones = MENU.filter(m => m.roles.includes(rol)).map(m => `
        <li class="nav-item">
            <a class="nav-link ${m.url === paginaActual ? 'active' : ''}" href="${m.url}">
                <i class="bi ${m.icono} me-1"></i>${m.texto}
            </a>
        </li>`).join('');

    document.getElementById('navbarSesion').innerHTML = `
        <ul class="navbar-nav flex-row flex-wrap gap-1 nav-modulo me-auto ms-lg-4">${opciones}</ul>
        <div class="d-flex align-items-center gap-3 text-white">
            <div class="text-end lh-sm">
                <div class="fw-semibold small"><i class="bi bi-person-circle me-1"></i>${USUARIO_ACTUAL.nombreCompleto}</div>
                <span class="badge-rol rol-${rol}">${rol}</span>
            </div>
            <form action="/logout" method="post" class="m-0">
                <button class="btn btn-sm btn-outline-light"><i class="bi bi-box-arrow-right me-1"></i>Salir</button>
            </form>
        </div>`;

    // Oculta cualquier elemento marcado con data-roles que no incluya el rol del usuario
    document.querySelectorAll('[data-roles]').forEach(el => {
        const permitidos = el.dataset.roles.split(',').map(r => r.trim());
        if (!permitidos.includes(rol)) el.remove();
    });

    return USUARIO_ACTUAL;
}

function tieneRol(...roles) {
    return USUARIO_ACTUAL && roles.includes(USUARIO_ACTUAL.rolNombre);
}

function mostrarAlerta(idContenedor, tipo, mensaje) {
    const icono = tipo === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill';
    document.getElementById(idContenedor).innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible d-flex align-items-center gap-2 fade show">
            <i class="bi ${icono} fs-5"></i><div>${mensaje}</div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
}

function escaparHtml(texto) {
    return String(texto ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}
