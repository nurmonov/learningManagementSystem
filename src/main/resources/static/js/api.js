const API_BASE_URL = 'http://localhost:8080';

class ApiService {
    constructor() {
        this.baseUrl = API_BASE_URL;
        this.initInterceptor();
    }

    // ==================== AUTH API ====================
    async login(username, password) {
        const loginData = { username, password };
        return this.request('/api/auth/login', {
            method: 'POST',
            body: loginData
        });
    }

    async register(username, email, password) {
        const registerData = { username, email, password };
        return this.request('/api/auth/register', {
            method: 'POST',
            body: registerData
        });
    }

    async refreshToken() {
        const refreshToken = localStorage.getItem('refreshToken');
        return this.request('/api/auth/refresh', {
            method: 'POST',
            body: { refreshToken }
        });
    }

    async logout() {
        return this.request('/api/auth/logout', {
            method: 'POST'
        });
    }

    async changePassword(oldPassword, newPassword) {
        return this.request('/api/auth/change-password', {
            method: 'POST',
            body: { oldPassword, newPassword }
        });
    }

    // ==================== USER API ====================
    async getUsers() {
        return this.request('/api/user');
    }

    async getUserById(id) {
        return this.request(`/api/user/${id}`);
    }

    async createUser(userData) {
        return this.request('/api/user', {
            method: 'POST',
            body: userData
        });
    }

    async updateUser(id, userData) {
        return this.request(`/api/user/${id}`, {
            method: 'PUT',
            body: userData
        });
    }

    async deleteUser(id) {
        return this.request(`/api/user/${id}`, {
            method: 'DELETE'
        });
    }

    async getUserProfile() {
        return this.request('/api/user/profile');
    }

    async updateUserProfile(userData) {
        return this.request('/api/user/profile', {
            method: 'PUT',
            body: userData
        });
    }

    // ==================== COURSE API ====================
    async getAllCourses() {
        return this.request('/api/course');
    }

    async getCourseById(id) {
        return this.request(`/api/course/${id}`);
    }

    async searchCourses(query) {
        return this.request(`/api/course/search?q=${encodeURIComponent(query)}`);
    }

    async createCourse(courseData) {
        return this.request('/api/course', {
            method: 'POST',
            body: courseData
        });
    }

    async updateCourse(id, courseData) {
        return this.request(`/api/course/${id}`, {
            method: 'PUT',
            body: courseData
        });
    }

    async deleteCourse(id) {
        return this.request(`/api/course/${id}`, {
            method: 'DELETE'
        });
    }

    // ==================== MODULE API ====================
    async getModules() {
        return this.request('/api/modules');
    }

    async getModuleById(id) {
        return this.request(`/api/modules/${id}`);
    }

    async createModule(moduleData) {
        return this.request('/api/modules', {
            method: 'POST',
            body: moduleData
        });
    }

    async updateModule(id, moduleData) {
        return this.request(`/api/modules/${id}`, {
            method: 'PUT',
            body: moduleData
        });
    }

    async deleteModule(id) {
        return this.request(`/api/modules/${id}`, {
            method: 'DELETE'
        });
    }

    async getCourseModules(courseId) {
        return this.request(`/api/course/${courseId}/modules`);
    }

    // ==================== LESSON API ====================
    async getLessons() {
        return this.request('/api/lesson');
    }

    async getLessonById(id) {
        return this.request(`/api/lesson/${id}`);
    }

    async createLesson(lessonData) {
        return this.request('/api/lesson', {
            method: 'POST',
            body: lessonData
        });
    }

    async updateLesson(id, lessonData) {
        return this.request(`/api/lesson/${id}`, {
            method: 'PUT',
            body: lessonData
        });
    }

    async deleteLesson(id) {
        return this.request(`/api/lesson/${id}`, {
            method: 'DELETE'
        });
    }

    async getModuleLessons(moduleId) {
        return this.request(`/api/modules/${moduleId}/lessons`);
    }

    async getLessonProgress(lessonId) {
        return this.request(`/api/lesson/${lessonId}/progress`);
    }

    async markLessonComplete(lessonId) {
        return this.request(`/api/lesson/${lessonId}/complete`, {
            method: 'POST'
        });
    }

    // ==================== ENROLLMENT API ====================
    async getEnrollments() {
        return this.request('/api/enrollments');
    }

    async getEnrollmentById(id) {
        return this.request(`/api/enrollments/${id}`);
    }

    async getUserEnrollments(userId) {
        return this.request(`/api/enrollments/user/${userId}`);
    }

    async createEnrollment(enrollmentData) {
        return this.request('/api/enrollments', {
            method: 'POST',
            body: enrollmentData
        });
    }

    async updateEnrollment(id, enrollmentData) {
        return this.request(`/api/enrollments/${id}`, {
            method: 'PUT',
            body: enrollmentData
        });
    }

    async deleteEnrollment(id) {
        return this.request(`/api/enrollments/${id}`, {
            method: 'DELETE'
        });
    }

    async getMyEnrollments() {
        const user = this.getCurrentUser();
        if (!user || !user.id) return [];
        return this.getUserEnrollments(user.id);
    }

    async enrollInCourse(courseId) {
        const user = this.getCurrentUser();
        if (!user || !user.id) throw new Error('User not authenticated');

        const enrollmentData = {
            courseId: courseId,
            userId: user.id,
            progress: 0,
            enrolledAt: new Date().toISOString()
        };

        return this.createEnrollment(enrollmentData);
    }

    // ==================== DASHBOARD API ====================
    async getDashboardData() {
        return this.request('/api/dashboard');
    }

    // ==================== HELPER METHODS ====================
    initInterceptor() {
        // Auto refresh token interceptor
        this.originalRequest = this.request;
        this.request = async (endpoint, options = {}) => {
            try {
                return await this.originalRequest(endpoint, options);
            } catch (error) {
                if (error.status === 401 && endpoint !== '/api/auth/refresh') {
                    try {
                        await this.refreshToken();
                        return await this.originalRequest(endpoint, options);
                    } catch (refreshError) {
                        this.clearAuth();
                        window.location.href = '/login.html';
                        throw refreshError;
                    }
                }
                throw error;
            }
        };
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;

        const headers = {
            'Content-Type': 'application/json',
        };

        // Add Authorization header
        const token = localStorage.getItem('token');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            headers,
            method: options.method || 'GET',
            body: options.body ? JSON.stringify(options.body) : undefined,
        };

        try {
            const response = await fetch(url, config);

            if (response.status === 204) {
                return { success: true, message: 'Operation successful' };
            }

            const contentType = response.headers.get('content-type');
            let data;

            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
            } else {
                const text = await response.text();
                try {
                    data = JSON.parse(text);
                } catch {
                    data = { message: text };
                }
            }

            if (!response.ok) {
                const error = new Error(data.message || `HTTP ${response.status}`);
                error.status = response.status;
                error.data = data;
                throw error;
            }

            return data;
        } catch (error) {
            console.error(`API Error [${endpoint}]:`, error);
            throw error;
        }
    }

    // Token management
    setAuthData(loginResponse) {
        localStorage.setItem('token', loginResponse.token);
        localStorage.setItem('refreshToken', loginResponse.refreshToken);
        localStorage.setItem('tokenType', loginResponse.tokenType || 'Bearer');
        this.decodeAndStoreUser(loginResponse.token);
    }

    decodeAndStoreUser(token) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const user = {
                id: payload.id || payload.sub,
                username: payload.username || payload.sub,
                email: payload.email,
                roles: payload.roles || ['USER']
            };
            localStorage.setItem('user', JSON.stringify(user));
        } catch (error) {
            console.error('Error decoding token:', error);
        }
    }

    clearAuth() {
        localStorage.removeItem('token');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('tokenType');
        localStorage.removeItem('user');
    }

    getCurrentUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    }

    isAuthenticated() {
        const token = localStorage.getItem('token');
        if (!token) return false;

        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const expiry = payload.exp * 1000;
            return Date.now() < expiry;
        } catch (error) {
            return false;
        }
    }

    isAdmin() {
        const user = this.getCurrentUser();
        return user && user.roles && user.roles.includes('ADMIN');
    }

    getAuthHeader() {
        const token = localStorage.getItem('token');
        const tokenType = localStorage.getItem('tokenType') || 'Bearer';
        return token ? `${tokenType} ${token}` : null;
    }
}

// Global API instance
const api = new ApiService();