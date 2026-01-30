class DashboardManager {
    constructor() {
        this.api = api;
        this.auth = authManager;
        this.currentUser = null;
        this.initDashboard();
    }

    async initDashboard() {
        try {
            // Check authentication
            this.auth.requireAuth();

            // Load user data
            await this.loadUserData();

            // Load dashboard data
            await this.loadDashboardData();

            // Load enrolled courses
            await this.loadEnrolledCourses();

            // Initialize chart
            this.initProgressChart();

            // Initialize event listeners
            this.initEventListeners();

        } catch (error) {
            console.error('Dashboard initialization error:', error);
            this.showError('Dashboard yuklashda xatolik yuz berdi');
        }
    }

    async loadUserData() {
        try {
            this.currentUser = this.api.getCurrentUser();
            if (!this.currentUser) {
                // If user data not in localStorage, redirect to login
                this.auth.logout();
                return;
            }

            // Update UI with user data
            this.updateUserUI();

        } catch (error) {
            console.error('Error loading user data:', error);
        }
    }

    updateUserUI() {
        if (!this.currentUser) return;

        // Update welcome message
        const welcomeElement = document.getElementById('userName');
        if (welcomeElement) {
            welcomeElement.textContent = this.currentUser.username;
        }

        // Update user info in header
        const userFullNameElement = document.getElementById('userFullName');
        if (userFullNameElement) {
            userFullNameElement.textContent = this.currentUser.username;
        }

        // Update user avatar
        const avatarElement = document.getElementById('userAvatar');
        if (avatarElement && this.currentUser.username) {
            const initials = this.currentUser.username.charAt(0).toUpperCase();
            avatarElement.src = `https://ui-avatars.com/api/?name=${initials}&background=4f46e5&color=fff`;
        }
    }

    async loadDashboardData() {
        try {
            // Get user enrollments to calculate stats
            const enrollments = await this.api.getUserEnrollments();

            // Calculate stats
            const stats = this.calculateStats(enrollments);

            // Update UI
            this.updateStatsUI(stats);

        } catch (error) {
            console.error('Error loading dashboard data:', error);
            // Use default stats
            this.updateStatsUI({
                courseCount: 0,
                completedCourses: 0,
                hoursStudied: 0,
                achievements: 0
            });
        }
    }

    calculateStats(enrollments) {
        let courseCount = 0;
        let completedCourses = 0;
        let totalProgress = 0;

        if (enrollments && Array.isArray(enrollments)) {
            courseCount = enrollments.length;
            completedCourses = enrollments.filter(e => e.progress === 100).length;
            totalProgress = enrollments.reduce((sum, e) => sum + (e.progress || 0), 0);
        }

        // Calculate hours studied (estimate: 10 hours per course on average)
        const hoursStudied = Math.round((totalProgress / 100) * courseCount * 10);

        // Calculate achievements (1 per completed course + bonus)
        const achievements = completedCourses * 2;

        return {
            courseCount,
            completedCourses,
            hoursStudied,
            achievements
        };
    }

    updateStatsUI(stats) {
        // Update course count
        const courseCountElement = document.getElementById('courseCount');
        if (courseCountElement) {
            courseCountElement.textContent = stats.courseCount;
        }

        // Update completed courses
        const completedCoursesElement = document.getElementById('completedCourses');
        if (completedCoursesElement) {
            completedCoursesElement.textContent = stats.completedCourses;
        }

        // Update hours studied
        const hoursStudiedElement = document.getElementById('hoursStudied');
        if (hoursStudiedElement) {
            hoursStudiedElement.textContent = stats.hoursStudied;
        }

        // Update achievements
        const achievementsElement = document.getElementById('achievements');
        if (achievementsElement) {
            achievementsElement.textContent = stats.achievements;
        }
    }

    async loadEnrolledCourses() {
        try {
            // Show loading state
            this.showCoursesLoading();

            // Get user enrollments
            const enrollments = await this.api.getUserEnrollments();

            if (!enrollments || enrollments.length === 0) {
                this.showNoCourses();
                return;
            }

            // Get course details for each enrollment
            const courses = [];
            for (const enrollment of enrollments.slice(0, 6)) { // Show only 6 recent courses
                try {
                    const course = await this.api.getCourseById(enrollment.courseId);
                    courses.push({
                        ...course,
                        progress: enrollment.progress || 0,
                        enrollmentId: enrollment.id
                    });
                } catch (error) {
                    console.error(`Error loading course ${enrollment.courseId}:`, error);
                }
            }

            // Display courses
            this.displayCourses(courses);

        } catch (error) {
            console.error('Error loading enrolled courses:', error);
            this.showNoCourses();
        }
    }

    showCoursesLoading() {
        const coursesGrid = document.getElementById('coursesGrid');
        if (coursesGrid) {
            coursesGrid.innerHTML = `
                <div class="loading-courses">
                    <div class="spinner"></div>
                    <p>Kurslar yuklanmoqda...</p>
                </div>
            `;
        }
    }

    showNoCourses() {
        const coursesGrid = document.getElementById('coursesGrid');
        if (coursesGrid) {
            coursesGrid.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-book-open"></i>
                    <h3>Hech qanday kurs topilmadi</h3>
                    <p>Hozircha kurslar mavjud emas. Bosh kurslarga qarang.</p>
                    <a href="courses.html" class="btn btn-primary" style="margin-top: 1rem;">
                        <i class="fas fa-search"></i> Kurslarni ko'rish
                    </a>
                </div>
            `;
        }
    }

    displayCourses(courses) {
        const coursesGrid = document.getElementById('coursesGrid');
        if (!coursesGrid) return;

        if (courses.length === 0) {
            this.showNoCourses();
            return;
        }

        coursesGrid.innerHTML = courses.map(course => `
            <div class="course-card" data-course-id="${course.id}">
                <div class="course-image">
                    <img src="${course.image || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'}" 
                         alt="${course.title}"
                         onerror="this.src='https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'">
                    <span class="course-category">${course.category || 'Umumiy'}</span>
                    <span class="course-level ${(course.level || 'beginner').toLowerCase()}">
                        ${course.level || 'Beginner'}
                    </span>
                </div>
                <div class="course-content">
                    <h3 class="course-title">${course.title}</h3>
                    <p class="course-description">${utils.truncateText(course.description || '', 80)}</p>
                    
                    <div class="course-progress">
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${course.progress || 0}%"></div>
                        </div>
                        <div class="progress-text">
                            <span>Progress</span>
                            <span>${course.progress || 0}%</span>
                        </div>
                    </div>
                    
                    <div class="course-actions">
                        <button onclick="dashboardManager.continueCourse(${course.id}, ${course.enrollmentId})" 
                                class="btn btn-primary btn-small">
                            <i class="fas fa-play"></i> Davom etish
                        </button>
                        <button onclick="dashboardManager.viewCourseDetails(${course.id})" 
                                class="btn btn-outline btn-small">
                            <i class="fas fa-info-circle"></i> Batafsil
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    }

    initProgressChart() {
        const ctx = document.getElementById('progressChart');
        if (!ctx) return;

        // Mock data for chart
        const data = {
            labels: ['Yan', 'Fev', 'Mar', 'Apr', 'May', 'Iyn'],
            datasets: [{
                label: 'Oʻqilgan soatlar',
                data: [12, 19, 15, 25, 22, 30],
                borderColor: '#4f46e5',
                backgroundColor: 'rgba(79, 70, 229, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.4
            }]
        };

        const config = {
            type: 'line',
            data: data,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            callback: function(value) {
                                return value + ' soat';
                            }
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    }
                }
            }
        };

        new Chart(ctx, config);
    }

    initEventListeners() {
        // User menu toggle
        const userInfo = document.querySelector('.user-info');
        if (userInfo) {
            userInfo.addEventListener('click', (e) => {
                e.stopPropagation();
                const menu = document.querySelector('.dropdown-menu');
                menu.classList.toggle('show');
            });
        }

        // Close dropdown when clicking outside
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.user-menu')) {
                const menu = document.querySelector('.dropdown-menu');
                if (menu) menu.classList.remove('show');
            }
        });

        // Search functionality
        const searchInput = document.querySelector('.search-box input');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    const query = e.target.value.trim();
                    if (query) {
                        window.location.href = `courses.html?search=${encodeURIComponent(query)}`;
                    }
                }
            });
        }

        // Logout button
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                this.auth.logout();
            });
        }
    }

    continueCourse(courseId, enrollmentId) {
        // For now, redirect to course details
        this.viewCourseDetails(courseId);
    }

    viewCourseDetails(courseId) {
        window.location.href = `course-details.html?id=${courseId}`;
    }

    showError(message) {
        utils.showToast(message, 'error');
    }
}

// Global dashboard manager
let dashboardManager;

// Initialize dashboard when page loads
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('dashboard.html')) {
        dashboardManager = new DashboardManager();
    }
});