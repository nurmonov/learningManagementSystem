class CoursesManager {
    constructor() {
        this.api = api;
        this.auth = authManager;
        this.currentUser = null;
        this.courses = [];
        this.filteredCourses = [];
        this.enrolledCourses = new Set();
        this.currentPage = 1;
        this.pageSize = 12;
        this.totalPages = 1;
        this.currentView = 'grid'; // 'grid' or 'list'
        this.filters = {
            category: '',
            level: '',
            sort: 'newest',
            search: ''
        };

        this.initCourses();
    }

    async initCourses() {
        try {
            this.auth.requireAuth();

            // Load user data
            await this.loadUserData();

            // Load all courses
            await this.loadCourses();

            // Load user enrollments
            await this.loadEnrollments();

            // Initialize event listeners
            this.initEventListeners();

        } catch (error) {
            console.error('Courses initialization error:', error);
            this.showError('Kurslar yuklashda xatolik yuz berdi');
        }
    }

    async loadUserData() {
        this.currentUser = this.api.getCurrentUser();
        this.updateUserUI();
    }

    updateUserUI() {
        if (!this.currentUser) return;

        const userFullNameElement = document.getElementById('userFullName');
        if (userFullNameElement) {
            userFullNameElement.textContent = this.currentUser.username;
        }

        const avatarElement = document.getElementById('userAvatar');
        if (avatarElement && this.currentUser.username) {
            const initials = this.currentUser.username.charAt(0).toUpperCase();
            avatarElement.src = `https://ui-avatars.com/api/?name=${initials}&background=4f46e5&color=fff`;
        }
    }

    async loadCourses() {
        try {
            this.showLoading();

            // Get all courses from API
            this.courses = await this.api.getAllCourses();

            // Apply filters and sort
            this.applyFilters();

            // Update UI
            this.updateCoursesUI();

        } catch (error) {
            console.error('Error loading courses:', error);
            this.showError('Kurslar yuklashda xatolik');
            this.courses = [];
            this.updateCoursesUI();
        }
    }

    async loadEnrollments() {
        try {
            const enrollments = await this.api.getMyEnrollments();
            this.enrolledCourses = new Set(enrollments.map(e => e.courseId));

            // Update courses UI if already loaded
            if (this.courses.length > 0) {
                this.updateCoursesUI();
            }

        } catch (error) {
            console.error('Error loading enrollments:', error);
        }
    }

    applyFilters() {
        let filtered = [...this.courses];

        // Apply search filter
        if (this.filters.search) {
            const searchLower = this.filters.search.toLowerCase();
            filtered = filtered.filter(course =>
                course.title.toLowerCase().includes(searchLower) ||
                (course.description && course.description.toLowerCase().includes(searchLower)) ||
                (course.category && course.category.toLowerCase().includes(searchLower))
            );
        }

        // Apply category filter
        if (this.filters.category) {
            filtered = filtered.filter(course =>
                course.category && course.category.toLowerCase() === this.filters.category.toLowerCase()
            );
        }

        // Apply level filter
        if (this.filters.level) {
            filtered = filtered.filter(course =>
                course.level && course.level.toLowerCase() === this.filters.level.toLowerCase()
            );
        }

        // Apply sorting
        filtered = this.sortCourses(filtered, this.filters.sort);

        this.filteredCourses = filtered;
        this.totalPages = Math.ceil(filtered.length / this.pageSize);
    }

    sortCourses(courses, sortType) {
        switch(sortType) {
            case 'newest':
                return courses.sort((a, b) =>
                    new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
                );
            case 'popular':
                return courses.sort((a, b) =>
                    (b.students || 0) - (a.students || 0)
                );
            case 'rating':
                return courses.sort((a, b) =>
                    (b.rating || 0) - (a.rating || 0)
                );
            case 'alphabetical':
                return courses.sort((a, b) =>
                    a.title.localeCompare(b.title)
                );
            default:
                return courses;
        }
    }

    updateCoursesUI() {
        this.updateCoursesCount();
        this.renderCourses();
        this.updatePagination();
    }

    updateCoursesCount() {
        const countElement = document.getElementById('coursesCount');
        if (countElement) {
            const count = this.filteredCourses.length;
            countElement.textContent = `${count} ta kurs topildi`;
        }
    }

    renderCourses() {
        const container = document.getElementById('coursesContainer');
        if (!container) return;

        if (this.filteredCourses.length === 0) {
            this.showNoCourses();
            return;
        }

        // Calculate paginated courses
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        const paginatedCourses = this.filteredCourses.slice(startIndex, endIndex);

        // Render based on view mode
        if (this.currentView === 'grid') {
            this.renderGridView(paginatedCourses, container);
        } else {
            this.renderListView(paginatedCourses, container);
        }
    }

    renderGridView(courses, container) {
        container.innerHTML = `
            <div class="courses-grid">
                ${courses.map(course => this.renderCourseCard(course)).join('')}
            </div>
        `;
    }

    renderListView(courses, container) {
        container.innerHTML = `
            <div class="courses-list">
                ${courses.map(course => this.renderCourseListItem(course)).join('')}
            </div>
        `;
    }

    renderCourseCard(course) {
        const isEnrolled = this.enrolledCourses.has(course.id);
        const levelClass = course.level ? course.level.toLowerCase() : 'beginner';
        const levelText = course.level || 'Beginner';

        return `
            <div class="course-card" data-course-id="${course.id}">
                <div class="course-image">
                    <img src="${course.image || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'}" 
                         alt="${course.title}"
                         onerror="this.src='https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'">
                    <div class="course-badges">
                        <span class="course-category">${course.category || 'Umumiy'}</span>
                        <span class="course-level ${levelClass}">${levelText}</span>
                    </div>
                </div>
                <div class="course-content">
                    <h3 class="course-title">${course.title}</h3>
                    
                    <div class="course-instructor">
                        <i class="fas fa-user-tie"></i>
                        ${course.instructor || 'Noma\'lum'}
                    </div>
                    
                    <p class="course-description">${utils.truncateText(course.description || 'Tavsif mavjud emas', 100)}</p>
                    
                    <div class="course-meta">
                        <span class="course-duration">
                            <i class="fas fa-clock"></i>
                            ${course.duration || 'N/A'}
                        </span>
                        <span class="course-students">
                            <i class="fas fa-users"></i>
                            ${course.students || 0}
                        </span>
                        <span class="course-rating">
                            <i class="fas fa-star"></i>
                            ${course.rating ? course.rating.toFixed(1) : 'N/A'}
                        </span>
                    </div>
                    
                    <div class="course-actions">
                        ${isEnrolled ?
            `<button class="btn btn-enrolled" disabled>
                                <i class="fas fa-check"></i> Ro'yxatdan o'tilgan
                            </button>` :
            `<button class="btn btn-primary" onclick="coursesManager.enrollInCourse(${course.id})">
                                <i class="fas fa-plus"></i> Ro'yxatdan o'tish
                            </button>`
        }
                        <button class="btn btn-outline" onclick="coursesManager.viewCourseDetails(${course.id})">
                            <i class="fas fa-info-circle"></i> Batafsil
                        </button>
                    </div>
                </div>
            </div>
        `;
    }

    renderCourseListItem(course) {
        const isEnrolled = this.enrolledCourses.has(course.id);
        const levelClass = course.level ? course.level.toLowerCase() : 'beginner';
        const levelText = course.level || 'Beginner';

        return `
            <div class="course-list-item" data-course-id="${course.id}">
                <div class="course-list-image">
                    <img src="${course.image || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=600&q=80'}" 
                         alt="${course.title}">
                </div>
                <div class="course-list-content">
                    <div class="course-list-header">
                        <h3 class="course-list-title">${course.title}</h3>
                        <div class="course-list-badges">
                            <span class="course-level ${levelClass}">${levelText}</span>
                            <span class="course-category">${course.category || 'Umumiy'}</span>
                        </div>
                    </div>
                    
                    <p class="course-list-description">${utils.truncateText(course.description || 'Tavsif mavjud emas', 200)}</p>
                    
                    <div class="course-list-footer">
                        <div class="course-list-meta">
                            <span class="course-list-meta-item">
                                <i class="fas fa-user-tie"></i>
                                ${course.instructor || 'Noma\'lum'}
                            </span>
                            <span class="course-list-meta-item">
                                <i class="fas fa-clock"></i>
                                ${course.duration || 'N/A'}
                            </span>
                            <span class="course-list-meta-item">
                                <i class="fas fa-users"></i>
                                ${course.students || 0}
                            </span>
                            <span class="course-list-meta-item">
                                <i class="fas fa-star"></i>
                                ${course.rating ? course.rating.toFixed(1) : 'N/A'}
                            </span>
                        </div>
                        
                        <div class="course-actions">
                            ${isEnrolled ?
            `<button class="btn btn-enrolled btn-small" disabled>
                                    <i class="fas fa-check"></i> Ro'yxatdan o'tilgan
                                </button>` :
            `<button class="btn btn-primary btn-small" onclick="coursesManager.enrollInCourse(${course.id})">
                                    <i class="fas fa-plus"></i> Ro'yxatdan o'tish
                                </button>`
        }
                            <button class="btn btn-outline btn-small" onclick="coursesManager.viewCourseDetails(${course.id})">
                                <i class="fas fa-info-circle"></i> Batafsil
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
    }

    updatePagination() {
        const pagination = document.getElementById('pagination');
        const currentPageElement = document.getElementById('currentPage');
        const totalPagesElement = document.getElementById('totalPages');
        const prevBtn = document.getElementById('prevPage');
        const nextBtn = document.getElementById('nextPage');

        if (!pagination || !currentPageElement || !totalPagesElement) return;

        if (this.totalPages <= 1) {
            pagination.style.display = 'none';
            return;
        }

        pagination.style.display = 'flex';
        currentPageElement.textContent = this.currentPage;
        totalPagesElement.textContent = this.totalPages;

        if (prevBtn) {
            prevBtn.disabled = this.currentPage === 1;
            prevBtn.onclick = () => this.goToPage(this.currentPage - 1);
        }

        if (nextBtn) {
            nextBtn.disabled = this.currentPage === this.totalPages;
            nextBtn.onclick = () => this.goToPage(this.currentPage + 1);
        }
    }

    goToPage(page) {
        if (page < 1 || page > this.totalPages) return;

        this.currentPage = page;
        this.renderCourses();
        this.updatePagination();

        // Scroll to top
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    showLoading() {
        const container = document.getElementById('coursesContainer');
        if (container) {
            container.innerHTML = `
                <div class="loading-courses">
                    <div class="spinner"></div>
                    <p>Kurslar yuklanmoqda...</p>
                </div>
            `;
        }
    }

    showNoCourses() {
        const container = document.getElementById('coursesContainer');
        const pagination = document.getElementById('pagination');

        if (container) {
            container.innerHTML = `
                <div class="empty-state">
                    <i class="fas fa-search"></i>
                    <h3>Hech qanday kurs topilmadi</h3>
                    <p>Qidiruv natijasiga mos kurslar mavjud emas.</p>
                    <button class="btn btn-primary" onclick="coursesManager.clearFilters()">
                        <i class="fas fa-times"></i> Filtrlarni tozalash
                    </button>
                </div>
            `;
        }

        if (pagination) {
            pagination.style.display = 'none';
        }
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

        // Search input
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            const debouncedSearch = utils.debounce((query) => {
                this.filters.search = query;
                this.currentPage = 1;
                this.applyFilters();
                this.updateCoursesUI();
            }, 500);

            searchInput.addEventListener('input', (e) => {
                debouncedSearch(e.target.value);
            });
        }

        // Category filter
        const categoryFilter = document.getElementById('categoryFilter');
        if (categoryFilter) {
            categoryFilter.addEventListener('change', (e) => {
                this.filters.category = e.target.value;
                this.currentPage = 1;
                this.applyFilters();
                this.updateCoursesUI();
            });
        }

        // Level filter
        const levelFilter = document.getElementById('levelFilter');
        if (levelFilter) {
            levelFilter.addEventListener('change', (e) => {
                this.filters.level = e.target.value;
                this.currentPage = 1;
                this.applyFilters();
                this.updateCoursesUI();
            });
        }

        // Sort filter
        const sortFilter = document.getElementById('sortFilter');
        if (sortFilter) {
            sortFilter.addEventListener('change', (e) => {
                this.filters.sort = e.target.value;
                this.applyFilters();
                this.updateCoursesUI();
            });
        }

        // View toggle buttons
        const viewButtons = document.querySelectorAll('.view-btn');
        viewButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const view = e.currentTarget.dataset.view;
                this.setView(view);
            });
        });

        // Logout button
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                this.auth.logout();
            });
        }

        // Check URL for search parameter
        this.checkUrlParams();
    }

    checkUrlParams() {
        const urlParams = new URLSearchParams(window.location.search);
        const searchParam = urlParams.get('search');

        if (searchParam) {
            const searchInput = document.getElementById('searchInput');
            if (searchInput) {
                searchInput.value = searchParam;
                this.filters.search = searchParam;
                this.applyFilters();
                this.updateCoursesUI();
            }
        }
    }

    setView(view) {
        if (view === this.currentView) return;

        this.currentView = view;

        // Update active button
        document.querySelectorAll('.view-btn').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.view === view) {
                btn.classList.add('active');
            }
        });

        // Re-render courses with new view
        this.renderCourses();
    }

    async enrollInCourse(courseId) {
        try {
            const course = this.courses.find(c => c.id === courseId);
            if (!course) return;

            const confirmed = confirm(`${course.title} kursiga ro'yxatdan o'tishni tasdiqlaysizmi?`);
            if (!confirmed) return;

            await this.api.enrollInCourse(courseId);

            // Update enrolled courses set
            this.enrolledCourses.add(courseId);

            // Show success message
            utils.showToast(`"${course.title}" kursiga muvaffaqiyatli ro'yxatdan o'tdingiz!`, 'success');

            // Update UI
            this.updateCoursesUI();

        } catch (error) {
            console.error('Enroll error:', error);
            const message = error.data?.message || 'Ro\'yxatdan o\'tishda xatolik yuz berdi';
            utils.showToast(message, 'error');
        }
    }

    viewCourseDetails(courseId) {
        window.location.href = `course-details.html?id=${courseId}`;
    }

    clearFilters() {
        // Reset filters
        this.filters = {
            category: '',
            level: '',
            sort: 'newest',
            search: ''
        };

        // Reset UI elements
        const searchInput = document.getElementById('searchInput');
        const categoryFilter = document.getElementById('categoryFilter');
        const levelFilter = document.getElementById('levelFilter');
        const sortFilter = document.getElementById('sortFilter');

        if (searchInput) searchInput.value = '';
        if (categoryFilter) categoryFilter.value = '';
        if (levelFilter) levelFilter.value = '';
        if (sortFilter) sortFilter.value = 'newest';

        // Reset page
        this.currentPage = 1;

        // Apply filters and update UI
        this.applyFilters();
        this.updateCoursesUI();

        // Clear URL parameters
        window.history.replaceState({}, document.title, window.location.pathname);

        utils.showToast('Filtrlar tozalandi', 'success');
    }

    showError(message) {
        utils.showToast(message, 'error');
    }
}

// Global courses manager
let coursesManager;

// Initialize courses when page loads
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('courses.html')) {
        coursesManager = new CoursesManager();
    }
});