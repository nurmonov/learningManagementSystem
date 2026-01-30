class CourseDetailsManager {
    constructor() {
        this.api = api;
        this.auth = authManager;
        this.courseId = null;
        this.course = null;
        this.modules = [];
        this.isEnrolled = false;
        this.enrollment = null;

        this.init();
    }

    async init() {
        try {
            this.auth.requireAuth();

            // Get course ID from URL
            this.courseId = this.getCourseIdFromUrl();
            if (!this.courseId) {
                this.showError('Kurs ID topilmadi');
                return;
            }

            // Load user data
            await this.loadUserData();

            // Load course details
            await this.loadCourseDetails();

            // Load modules
            await this.loadModules();

            // Check enrollment
            await this.checkEnrollment();

            // Initialize event listeners
            this.initEventListeners();

        } catch (error) {
            console.error('Course details initialization error:', error);
            this.showError('Kurs ma\'lumotlarini yuklashda xatolik');
        }
    }

    getCourseIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    async loadUserData() {
        const user = this.api.getCurrentUser();
        this.updateUserUI(user);
    }

    updateUserUI(user) {
        if (!user) return;

        const userFullNameElement = document.getElementById('userFullName');
        if (userFullNameElement) {
            userFullNameElement.textContent = user.username;
        }

        const avatarElement = document.getElementById('userAvatar');
        if (avatarElement && user.username) {
            const initials = user.username.charAt(0).toUpperCase();
            avatarElement.src = `https://ui-avatars.com/api/?name=${initials}&background=4f46e5&color=fff`;
        }
    }

    async loadCourseDetails() {
        try {
            this.course = await this.api.getCourseById(this.courseId);
            this.renderCourseDetails();

        } catch (error) {
            console.error('Error loading course details:', error);
            this.showError('Kurs ma\'lumotlarini yuklashda xatolik');
        }
    }

    async loadModules() {
        try {
            this.modules = await this.api.getCourseModules(this.courseId);
            this.renderModules();

        } catch (error) {
            console.error('Error loading modules:', error);
            this.modules = [];
        }
    }

    async checkEnrollment() {
        try {
            const enrollments = await this.api.getMyEnrollments();
            this.enrollment = enrollments.find(e => e.courseId == this.courseId);
            this.isEnrolled = !!this.enrollment;
            this.updateEnrollmentUI();

        } catch (error) {
            console.error('Error checking enrollment:', error);
        }
    }

    renderCourseDetails() {
        const container = document.getElementById('courseDetails');
        if (!container || !this.course) return;

        const levelClass = this.course.level ? this.course.level.toLowerCase() : 'beginner';
        const levelText = this.course.level || 'Beginner';

        container.innerHTML = `
            <div class="course-details">
                <!-- Course Header -->
                <div class="course-header">
                    <div class="course-hero">
                        <img src="${this.course.image || 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?ixlib=rb-4.0.3&auto=format&fit=crop&w=1200&q=80'}" 
                             alt="${this.course.title}" class="course-hero-image">
                        <div class="course-hero-overlay">
                            <div class="course-hero-content">
                                <div class="course-badges">
                                    <span class="course-category">${this.course.category || 'Umumiy'}</span>
                                    <span class="course-level ${levelClass}">${levelText}</span>
                                </div>
                                <h1 class="course-title">${this.course.title}</h1>
                                <p class="course-instructor">
                                    <i class="fas fa-user-tie"></i>
                                    ${this.course.instructor || 'Noma\'lum o\'qituvchi'}
                                </p>
                            </div>
                        </div>
                    </div>
                    
                    <div class="course-actions" id="courseActions">
                        <!-- Actions will be loaded here -->
                    </div>
                </div>
                
                <!-- Course Content -->
                <div class="course-content-section">
                    <div class="course-sidebar">
                        <div class="course-info-card">
                            <h3>Kurs haqida</h3>
                            <div class="info-item">
                                <i class="fas fa-clock"></i>
                                <div>
                                    <span class="info-label">Davomiylik</span>
                                    <span class="info-value">${this.course.duration || 'Noma\'lum'}</span>
                                </div>
                            </div>
                            <div class="info-item">
                                <i class="fas fa-users"></i>
                                <div>
                                    <span class="info-label">O'quvchilar</span>
                                    <span class="info-value">${this.course.students || 0} ta</span>
                                </div>
                            </div>
                            <div class="info-item">
                                <i class="fas fa-star"></i>
                                <div>
                                    <span class="info-label">Reyting</span>
                                    <span class="info-value">${this.course.rating ? this.course.rating.toFixed(1) : 'N/A'}</span>
                                </div>
                            </div>
                            <div class="info-item">
                                <i class="fas fa-layer-group"></i>
                                <div>
                                    <span class="info-label">Darajasi</span>
                                    <span class="info-value">${levelText}</span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="course-progress-card" id="progressCard" style="display: none;">
                            <h3>Progress</h3>
                            <div class="progress-circle">
                                <div class="circle">
                                    <span class="progress-percentage">0%</span>
                                </div>
                            </div>
                            <button class="btn btn-primary btn-block" onclick="courseDetails.continueLearning()">
                                <i class="fas fa-play"></i> O'rganishni davom ettirish
                            </button>
                        </div>
                    </div>
                    
                    <div class="course-main">
                        <!-- Course Description -->
                        <div class="course-description-section">
                            <h2>Kurs haqida</h2>
                            <div class="description-content">
                                ${this.course.description || 'Kurs tavsifi mavjud emas.'}
                            </div>
                        </div>
                        
                        <!-- Modules -->
                        <div class="course-modules-section">
                            <div class="section-header">
                                <h2>Kurs modullari</h2>
                                <span class="module-count">${this.modules.length} ta modul</span>
                            </div>
                            
                            <div class="modules-container" id="modulesContainer">
                                <!-- Modules will be loaded here -->
                                <div class="loading-modules">
                                    <div class="spinner"></div>
                                    <p>Modullar yuklanmoqda...</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        this.updateEnrollmentUI();
    }

    updateEnrollmentUI() {
        const actionsContainer = document.getElementById('courseActions');
        const progressCard = document.getElementById('progressCard');

        if (!actionsContainer) return;

        if (this.isEnrolled) {
            actionsContainer.innerHTML = `
                <button class="btn btn-primary btn-large" onclick="courseDetails.continueLearning()">
                    <i class="fas fa-play"></i> O'rganishni davom ettirish
                </button>
                <button class="btn btn-outline" onclick="courseDetails.leaveCourse()">
                    <i class="fas fa-sign-out-alt"></i> Kursdan chiqish
                </button>
                <button class="btn btn-icon" onclick="courseDetails.shareCourse()" title="Ulashish">
                    <i class="fas fa-share-alt"></i>
                </button>
            `;

            if (progressCard) {
                progressCard.style.display = 'block';
                this.updateProgress();
            }
        } else {
            actionsContainer.innerHTML = `
                <button class="btn btn-primary btn-large" onclick="courseDetails.enrollInCourse()">
                    <i class="fas fa-plus"></i> Kursga ro'yxatdan o'tish
                </button>
                <button class="btn btn-outline" onclick="courseDetails.addToWishlist()">
                    <i class="far fa-heart"></i> Istaklar ro'yxatiga qo'shish
                </button>
                <button class="btn btn-icon" onclick="courseDetails.shareCourse()" title="Ulashish">
                    <i class="fas fa-share-alt"></i>
                </button>
            `;

            if (progressCard) {
                progressCard.style.display = 'none';
            }
        }
    }

    renderModules() {
        const container = document.getElementById('modulesContainer');
        if (!container) return;

        if (this.modules.length === 0) {
            container.innerHTML = `
                <div class="empty-modules">
                    <i class="fas fa-book-open"></i>
                    <p>Bu kursda hozircha modullar mavjud emas</p>
                </div>
            `;
            return;
        }

        container.innerHTML = this.modules.map((module, index) => `
            <div class="module-card" data-module-id="${module.id}">
                <div class="module-header">
                    <div class="module-number">${index + 1}</div>
                    <div class="module-info">
                        <h3 class="module-title">${module.title}</h3>
                        <p class="module-lesson-count">${module.lessons ? module.lessons.length : 0} ta dars</p>
                    </div>
                    <button class="module-toggle" onclick="courseDetails.toggleModule(${module.id})">
                        <i class="fas fa-chevron-down"></i>
                    </button>
                </div>
                <div class="module-content" id="module-${module.id}" style="display: none;">
                    <div class="module-description">
                        ${module.description || 'Modul tavsifi mavjud emas.'}
                    </div>
                    <div class="module-lessons" id="lessons-${module.id}">
                        <!-- Lessons will be loaded here -->
                        <div class="loading-lessons">
                            <div class="spinner small"></div>
                            <span>Darslar yuklanmoqda...</span>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');
    }

    async toggleModule(moduleId) {
        const moduleContent = document.getElementById(`module-${moduleId}`);
        const toggleButton = document.querySelector(`[data-module-id="${moduleId}"] .module-toggle i`);
        const lessonsContainer = document.getElementById(`lessons-${moduleId}`);

        if (!moduleContent || !toggleButton) return;

        if (moduleContent.style.display === 'none') {
            // Show module
            moduleContent.style.display = 'block';
            toggleButton.className = 'fas fa-chevron-up';

            // Load lessons if not loaded
            if (lessonsContainer.querySelector('.loading-lessons')) {
                await this.loadModuleLessons(moduleId, lessonsContainer);
            }
        } else {
            // Hide module
            moduleContent.style.display = 'none';
            toggleButton.className = 'fas fa-chevron-down';
        }
    }

    async loadModuleLessons(moduleId, container) {
        try {
            const lessons = await this.api.getModuleLessons(moduleId);

            if (lessons.length === 0) {
                container.innerHTML = '<p class="no-lessons">Bu modulda hozircha darslar mavjud emas</p>';
                return;
            }

            // Get completed lessons
            let completedLessons = [];
            if (this.enrollment && this.enrollment.completedLessons) {
                completedLessons = this.enrollment.completedLessons;
            }

            container.innerHTML = lessons.map((lesson, index) => {
                const isCompleted = completedLessons.includes(lesson.id);
                const isLocked = !this.isEnrolled && lesson.locked;
                const canAccess = this.isEnrolled || !lesson.locked;

                return `
                    <div class="lesson-item ${isLocked ? 'locked' : ''} ${isCompleted ? 'completed' : ''}">
                        <div class="lesson-info">
                            <div class="lesson-icon">
                                ${isCompleted ?
                    '<i class="fas fa-check-circle"></i>' :
                    isLocked ?
                        '<i class="fas fa-lock"></i>' :
                        '<i class="fas fa-play-circle"></i>'
                }
                            </div>
                            <div class="lesson-details">
                                <h4 class="lesson-title">${lesson.title}</h4>
                                <div class="lesson-meta">
                                    <span class="lesson-duration">
                                        <i class="fas fa-clock"></i> ${lesson.duration || 'N/A'}
                                    </span>
                                    <span class="lesson-type">
                                        <i class="fas fa-file-alt"></i> ${lesson.type || 'Dars'}
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="lesson-actions">
                            ${canAccess ?
                    `<button class="btn btn-outline btn-small" onclick="courseDetails.startLesson(${lesson.id})">
                                    ${isCompleted ?
                        '<i class="fas fa-redo"></i> Qayta ko\'rish' :
                        '<i class="fas fa-play"></i> Darsni boshlash'
                    }
                                </button>` :
                    '<span class="locked-text">Ro\'yxatdan o\'ting</span>'
                }
                        </div>
                    </div>
                `;
            }).join('');

        } catch (error) {
            console.error('Error loading lessons:', error);
            container.innerHTML = '<p class="error-text">Darslarni yuklashda xatolik yuz berdi</p>';
        }
    }

    updateProgress() {
        const progressPercentage = this.enrollment ? this.enrollment.progress || 0 : 0;
        const progressElement = document.querySelector('.progress-percentage');

        if (progressElement) {
            progressElement.textContent = `${progressPercentage}%`;
        }

        // Update progress circle
        const circle = document.querySelector('.progress-circle .circle');
        if (circle) {
            const circumference = 2 * Math.PI * 50; // Radius = 50
            const offset = circumference - (progressPercentage / 100) * circumference;
            circle.style.strokeDasharray = `${circumference} ${circumference}`;
            circle.style.strokeDashoffset = offset;
        }
    }

    async enrollInCourse() {
        try {
            if (!this.course) return;

            const confirmed = confirm(`${this.course.title} kursiga ro'yxatdan o'tishni tasdiqlaysizmi?`);
            if (!confirmed) return;

            await this.api.enrollInCourse(this.courseId);

            this.isEnrolled = true;
            this.enrollment = {
                courseId: this.courseId,
                progress: 0,
                enrolledAt: new Date().toISOString()
            };

            this.updateEnrollmentUI();
            utils.showToast(`"${this.course.title}" kursiga muvaffaqiyatli ro'yxatdan o'tdingiz!`, 'success');

        } catch (error) {
            console.error('Enroll error:', error);
            const message = error.data?.message || 'Ro\'yxatdan o\'tishda xatolik yuz berdi';
            utils.showToast(message, 'error');
        }
    }

    async leaveCourse() {
        try {
            if (!this.enrollment) return;

            const confirmed = confirm(`${this.course.title} kursidan chiqishni tasdiqlaysizmi?`);
            if (!confirmed) return;

            await this.api.unenrollFromCourse(this.enrollment.id);

            this.isEnrolled = false;
            this.enrollment = null;

            this.updateEnrollmentUI();
            utils.showToast(`"${this.course.title}" kursidan chiqdingiz`, 'success');

        } catch (error) {
            console.error('Leave course error:', error);
            utils.showToast('Kursdan chiqishda xatolik', 'error');
        }
    }

    continueLearning() {
        if (!this.isEnrolled) {
            this.enrollInCourse();
            return;
        }

        // Find first incomplete lesson
        this.findFirstIncompleteLesson();
    }

    async findFirstIncompleteLesson() {
        try {
            let targetLesson = null;
            let targetModule = null;

            // Search through modules and lessons
            for (const module of this.modules) {
                try {
                    const lessons = await this.api.getModuleLessons(module.id);

                    for (const lesson of lessons) {
                        // Check if lesson is accessible and not completed
                        if (!lesson.locked) {
                            const isCompleted = this.enrollment && this.enrollment.completedLessons &&
                                this.enrollment.completedLessons.includes(lesson.id);

                            if (!isCompleted) {
                                targetLesson = lesson;
                                targetModule = module;
                                break;
                            }
                        }
                    }

                    if (targetLesson) break;
                } catch (error) {
                    console.error(`Error loading lessons for module ${module.id}:`, error);
                }
            }

            if (targetLesson) {
                this.startLesson(targetLesson.id);
            } else {
                // All lessons completed or no lessons found
                utils.showToast('Barcha darslarni tugatdingiz!', 'success');
            }

        } catch (error) {
            console.error('Error finding incomplete lesson:', error);
        }
    }

    startLesson(lessonId) {
        if (!this.isEnrolled) {
            utils.showToast('Darsni boshlash uchun avval kursga ro\'yxatdan o\'ting', 'warning');
            return;
        }

        // Redirect to lesson page
        window.location.href = `lesson.html?courseId=${this.courseId}&lessonId=${lessonId}`;
    }

    addToWishlist() {
        // For now, just show a message
        utils.showToast('Kurs istaklar ro\'yxatiga qo\'shildi', 'success');

        // In a real app, you would make an API call here
        // await this.api.addToWishlist(this.courseId);
    }

    shareCourse() {
        const shareUrl = `${window.location.origin}/course-details.html?id=${this.courseId}`;
        const shareText = `Men bu ajoyib kursni topdim: ${this.course.title}`;

        if (navigator.share) {
            navigator.share({
                title: this.course.title,
                text: shareText,
                url: shareUrl
            }).catch(console.error);
        } else {
            // Fallback: Copy to clipboard
            navigator.clipboard.writeText(shareUrl).then(() => {
                utils.showToast('Link nusxalandi!', 'success');
            }).catch(console.error);
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

        // Logout button
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => {
                this.auth.logout();
            });
        }
    }

    showError(message) {
        const container = document.getElementById('courseDetails');
        if (container) {
            container.innerHTML = `
                <div class="error-state">
                    <i class="fas fa-exclamation-circle"></i>
                    <h3>Xatolik yuz berdi</h3>
                    <p>${message}</p>
                    <button class="btn btn-primary" onclick="window.history.back()">
                        <i class="fas fa-arrow-left"></i> Orqaga qaytish
                    </button>
                </div>
            `;
        }
    }
}

// Global course details manager
let courseDetails;

// Initialize when page loads
document.addEventListener('DOMContentLoaded', () => {
    if (window.location.pathname.includes('course-details.html')) {
        courseDetails = new CourseDetailsManager();
    }
});