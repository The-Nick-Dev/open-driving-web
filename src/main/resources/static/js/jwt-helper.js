// JWT Helper - Manejo de token y llamadas API

const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'user_info';

// Inyectar CSS del spinner una sola vez al cargar el script
(function () {
    const s = document.createElement('style');
    s.textContent = `
        #loading-overlay {
            position: fixed; inset: 0;
            background: rgba(255,255,255,1);
            display: flex; align-items: center; justify-content: center;
            z-index: 9999;
        }
        .loading-spinner {
            width: 48px; height: 48px;
            border: 5px solid #e0e0e0;
            border-top-color: #667eea;
            border-radius: 50%;
            animation: _spin 0.75s linear infinite;
        }
        @keyframes _spin { to { transform: rotate(360deg); } }
    `;
    document.head.appendChild(s);
})();

function showLoading() {
    if (document.getElementById('loading-overlay')) return;
    const el = document.createElement('div');
    el.id = 'loading-overlay';
    el.innerHTML = '<div class="loading-spinner"></div>';
    document.body.appendChild(el);
}

function hideLoading() {
    document.getElementById('loading-overlay')?.remove();
}

// Guardar token y datos de usuario
function saveToken(token, user) {
    sessionStorage.setItem(TOKEN_KEY, token);
    sessionStorage.setItem(USER_KEY, JSON.stringify(user));
}

// Obtener token
function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
}

// Obtener info de usuario
function getUser() {
    const data = sessionStorage.getItem(USER_KEY);
    return data ? JSON.parse(data) : null;
}

// Eliminar token (logout)
function removeToken() {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(USER_KEY);
}

// Verificar si está autenticado
function isAuthenticated() {
    return !!getToken();
}

// Obtener nombre del rol (sin prefijo ROLE_)
function getRole() {
    const user = getUser();
    if (!user) return null;
    return user.rol ? user.rol.replace('ROLE_', '') : null;
}

// Redirigir según rol después del login
function redirectByRole() {
    const role = getRole();
    const validRoles = ['ADMIN', 'PROFESOR', 'ALUMNO'];
    
    if (!role || !validRoles.includes(role)) {
        console.error('Rol inválido o inexistente:', role);
        removeToken();
        return;
    }
    
    if (role === 'ADMIN') {
        window.location.href = '/admin/dashboard';
    } else if (role === 'PROFESOR') {
        window.location.href = '/profesor/temas';
    } else if (role === 'ALUMNO') {
        window.location.href = '/alumno/dashboard';
    }
}

// Control de bucles de redirección
function checkRedirectLoop() {
    const lastRedirect = sessionStorage.getItem('last_redirect_time');
    const now = Date.now();
    
    if (lastRedirect && (now - parseInt(lastRedirect)) < 2000) {
        // Menos de 2 segundos entre redirecciones = posible bucle
        const count = parseInt(sessionStorage.getItem('redirect_loop_count') || '0');
        if (count > 3) {
            console.error('Bucle de redirección detectado, limpiando token');
            removeToken();
            sessionStorage.removeItem('redirect_loop_count');
            sessionStorage.removeItem('last_redirect_time');
            return false;
        }
        sessionStorage.setItem('redirect_loop_count', (count + 1).toString());
    } else {
        sessionStorage.setItem('redirect_loop_count', '1');
    }
    sessionStorage.setItem('last_redirect_time', now.toString());
    return true;
}

// Redirigir a login si no hay token
function checkAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

// Obtener nombre del rol (sin prefijo ROLE_)
function getRole() {
    const user = getUser();
    if (!user) return null;
    return user.rol ? user.rol.replace('ROLE_', '') : null;
}

// Redirigir según rol después del login
function redirectByRole() {
    const role = getRole();
    if (role === 'ADMIN') {
        window.location.href = '/admin/dashboard';
    } else if (role === 'PROFESOR') {
        window.location.href = '/profesor/temas';
    } else if (role === 'ALUMNO') {
        window.location.href = '/alumno/dashboard';
    } else {
        console.error('Rol no reconocido:', role);
        removeToken();
        if (window.location.pathname !== '/login') {
            window.location.href = '/login';
        }
    }
}

// apiFetch: wrapper de fetch que añade el header Authorization
async function apiFetch(url, options = {}) {
    const token = getToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    
    const response = await fetch(url, {
        ...options,
        headers: headers
    });
    
    if (response.status === 401) {
        removeToken();
        window.location.href = '/login';
        return null;
    }
    
    return response;
}

// Login: hace POST a /api/auth/login y guarda el token
async function login(username, password) {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: username, password: password })
    });
    
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        
        throw new Error(errorData.error || 'Credenciales inválidas');
    }
    
    const data = await response.json();
    saveToken(data.token, {
        username: data.username,
        nombre: data.nombre,
        rol: data.rol
    });
    
    return data;
}

// Logout
function logout() {
    removeToken();
    window.location.href = '/login';
}

// Cargar datos del usuario actual desde el API
async function loadCurrentUser() {
    const response = await apiFetch('/api/auth/me');
    if (response && response.ok) {
        const data = await response.json();
        saveToken(data.token, {
            username: data.username,
            nombre: data.nombre,
            rol: data.rol
        });
        return data;
    }
    return null;
}
