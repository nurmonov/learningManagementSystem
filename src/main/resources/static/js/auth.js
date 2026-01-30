class AuthManager {
    constructor() {
        this.api = api;
        this.initEventListeners();
        this.checkAuthState();
    }

    initEventListeners() {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Register form
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', (e) => this.handleRegister(e));
        }

        // Password toggle
        const toggleButtons = document.querySelectorAll('.toggle-password');
        toggleButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const input = e.target.closest('.password-input').querySelector('input');
                this.togglePasswordVisibility(input, e.target);
            });
        });

        // Auto fill saved username/email
        const savedUsername = localStorage.getItem('rememberUsername');
        if (savedUsername) {
            const usernameInput = document.getElementById('username');
            if (usernameInput) usernameInput.value = savedUsername;
        }
    }

    checkAuthState() {
        // If user is already logged in and on auth pages, redirect to dashboard
        if (this.isLoggedIn()) {
            const currentPage = window.location.pathname;
            if (currentPage.includes('login.html') ||
                currentPage.includes('register.html') ||
                currentPage === '/' ||
                currentPage.includes('index.html')) {
                window.location.href = 'dashboard.html';
            }
        }
    }

    async handleLogin(e) {
        e.preventDefault();

        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const rememberCheckbox = document.getElementById('remember');

        if (!usernameInput || !passwordInput) {
            this.showAlert('loginAlert', 'Form not properly initialized', 'danger');
            return;
        }

        const username = usernameInput.value.trim();
        const password = passwordInput.value;
        const remember = rememberCheckbox?.checked;

        // Validate
        if (!username) {
            this.showError('usernameError', 'Foydalanuvchi nomi yoki email kiriting');
            return;
        }

        if (!password) {
            this.showError('passwordError', 'Parol kiriting');
            return;
        }

        try {
            this.showLoading('loginBtn');
            this.hideAlert('loginAlert');

            // LoginRequestDTO: {username, password}
            const response = await this.api.login(username, password);

            // Save auth data
            this.api.setAuthData(response);

            // Remember username
            if (remember) {
                localStorage.setItem('rememberUsername', username);
            } else {
                localStorage.removeItem('rememberUsername');
            }

            this.showAlert('loginAlert', 'Muvaffaqiyatli kirdingiz!', 'success');

            // Redirect to dashboard after 1 second
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1000);

        } catch (error) {
            console.error('Login error:', error);
            const message = error.data?.message ||
                error.message ||
                'Login yoki parol noto\'g\'ri';
            this.showAlert('loginAlert', message, 'danger');
        } finally {
            this.hideLoading('loginBtn');
        }
    }

    async handleRegister(e) {
        e.preventDefault();

        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword')?.value;
        const termsAccepted = document.getElementById('terms')?.checked;

        // Validation
        const errors = this.validateRegistration({username, email, password, confirmPassword, termsAccepted});
        if (errors.length > 0) {
            errors.forEach(error => this.showAlert('registerAlert', error, 'danger'));
            return;
        }

        try {
            this.showLoading('registerBtn');
            this.hideAlert('registerAlert');

            // RegisterRequestDTO: {username, email, password}
            await this.api.register(username, email, password);

            this.showAlert('registerAlert',
                'Muvaffaqiyatli ro\'yxatdan o\'tdingiz! Endi tizimga kiring.',
                'success'
            );

            // Reset form
            e.target.reset();

            // Redirect to login page after 3 seconds
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 3000);

        } catch (error) {
            console.error('Register error:', error);
            const message = error.data?.message ||
                error.message ||
                'Ro\'yxatdan o\'tishda xatolik yuz berdi';
            this.showAlert('registerAlert', message, 'danger');
        } finally {
            this.hideLoading('registerBtn');
        }
    }

    validateRegistration(data) {
        const errors = [];

        // Username validation
        if (!data.username) {
            errors.push('Foydalanuvchi nomi kiriting');
        } else if (data.username.length < 3) {
            errors.push('Foydalanuvchi nomi kamida 3 ta belgidan iborat bo\'lishi kerak');
        }

        // Email validation
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!data.email) {
            errors.push('Email manzil kiriting');
        } else if (!emailRegex.test(data.email)) {
            errors.push('Iltimos, to\'g\'ri email kiriting');
        }

        // Password validation
        if (!data.password) {
            errors.push('Parol kiriting');
        } else if (data.password.length < 6) {
            errors.push('Parol kamida 6 ta belgidan iborat bo\'lishi kerak');
        }

        // Confirm password
        if (data.confirmPassword && data.password !== data.confirmPassword) {
            errors.push('Parollar mos kelmadi');
        }

        // Terms acceptance
        if (!data.termsAccepted) {
            errors.push('Foydalanish shartlari bilan rozilik bildirishingiz kerak');
        }

        return errors;
    }

    togglePasswordVisibility(input, button) {
        const icon = button.querySelector('i');
        if (input.type === 'password') {
            input.type = 'text';
            icon.className = 'fas fa-eye-slash';
        } else {
            input.type = 'password';
            icon.className = 'fas fa-eye';
        }
    }

    showLoading(buttonId) {
        const button = document.getElementById(buttonId);
        if (button) {
            const text = button.querySelector('.btn-text');
            const spinner = button.querySelector('.btn-spinner');

            if (text) text.style.display = 'none';
            if (spinner) spinner.style.display = 'inline-block';
            button.disabled = true;
        }
    }

    hideLoading(buttonId) {
        const button = document.getElementById(buttonId);
        if (button) {
            const text = button.querySelector('.btn-text');
            const spinner = button.querySelector('.btn-spinner');

            if (text) text.style.display = 'inline-block';
            if (spinner) spinner.style.display = 'none';
            button.disabled = false;
        }
    }

    showAlert(alertId, message, type = 'danger') {
        const alertElement = document.getElementById(alertId);
        if (alertElement) {
            alertElement.innerHTML = `
                <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
                ${message}
            `;
            alertElement.className = `alert alert-${type} show`;
            alertElement.style.display = 'flex';

            // Auto hide after 5 seconds
            setTimeout(() => {
                this.hideAlert(alertId);
            }, 5000);
        }
    }

    hideAlert(alertId) {
        const alertElement = document.getElementById(alertId);
        if (alertElement) {
            alertElement.style.display = 'none';
        }
    }

    showError(elementId, message) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = message;
            element.classList.add('show');
        }
    }

    hideError(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = '';
            element.classList.remove('show');
        }
    }

    // Check if user is logged in
    isLoggedIn() {
        return this.api.isAuthenticated();
    }

    // Logout
    async logout() {
        try {
            await this.api.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            this.api.clearAuth();
            window.location.href = 'login.html';
        }
    }

    // Get current user
    getCurrentUser() {
        return this.api.getCurrentUser();
    }

    // Require authentication (for protected pages)
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = 'login.html';
        }
    }
}

// Global auth manager
const authManager = new AuthManager();

// Auto check auth on page load
document.addEventListener('DOMContentLoaded', () => {
    // Check if we're on a protected page
    const protectedPages = ['dashboard.html', 'profile.html', 'courses.html'];
    const currentPage = window.location.pathname.split('/').pop();

    if (protectedPages.includes(currentPage)) {
        authManager.requireAuth();
    }
});