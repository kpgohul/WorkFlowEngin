/**
 * Session Management Utility
 * Handles session expiration detection and user notification
 */
class SessionManager {
    constructor() {
        this.sessionTimeoutWarning = 5 * 60 * 1000; // 5 minutes before timeout
        this.sessionTimeout = 30 * 60 * 1000; // 30 minutes session timeout
        this.warningShown = false;
        this.init();
    }

    init() {
        this.startSessionTimer();
        this.setupActivityListeners();
        this.checkForSessionExpiredParam();
    }

    startSessionTimer() {
        // Clear existing timers
        if (this.warningTimer) clearTimeout(this.warningTimer);
        if (this.timeoutTimer) clearTimeout(this.timeoutTimer);

        // Set warning timer
        this.warningTimer = setTimeout(() => {
            this.showSessionWarning();
        }, this.sessionTimeout - this.sessionTimeoutWarning);

        // Set timeout timer
        this.timeoutTimer = setTimeout(() => {
            this.handleSessionTimeout();
        }, this.sessionTimeout);
    }

    showSessionWarning() {
        if (this.warningShown) return;

        this.warningShown = true;

        const warningDiv = document.createElement('div');
        warningDiv.className = 'alert alert-warning session-warning';
        warningDiv.id = 'session-warning';
        warningDiv.innerHTML = `
            <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>Your session will expire in 5 minutes. Click to extend your session.</span>
                <div>
                    <button onclick="sessionManager.extendSession()" class="btn-link">Extend Session</button>
                    <button onclick="sessionManager.dismissWarning()" class="btn-link" style="margin-left: 10px;">×</button>
                </div>
            </div>
        `;

        // Insert at the top of the main content
        const mainContent = document.querySelector('main') || document.body;
        mainContent.insertBefore(warningDiv, mainContent.firstChild);

        // Auto-dismiss warning after 30 seconds if not acted upon
        setTimeout(() => {
            this.dismissWarning();
        }, 30000);
    }

    extendSession() {
        // Make a light request to extend session
        fetch('/api/v1/auth/session-extend', {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                this.dismissWarning();
                this.warningShown = false;
                this.startSessionTimer(); // Restart timer
                this.showSuccessMessage('Session extended successfully');
            } else {
                this.handleSessionTimeout();
            }
        })
        .catch(() => {
            this.handleSessionTimeout();
        });
    }

    dismissWarning() {
        const warning = document.getElementById('session-warning');
        if (warning) {
            warning.remove();
        }
        this.warningShown = false;
    }

    handleSessionTimeout() {
        // Clear any existing warnings
        this.dismissWarning();

        // Show timeout message
        this.showTimeoutMessage();

        // Redirect to login after a short delay
        setTimeout(() => {
            window.location.href = '/api/v1/auth/login?sessionExpired=true';
        }, 3000);
    }

    showTimeoutMessage() {
        const timeoutDiv = document.createElement('div');
        timeoutDiv.className = 'alert alert-error session-timeout';
        timeoutDiv.innerHTML = `
            <div style="text-align: center;">
                <strong>Session Expired</strong><br>
                Your session has expired for security reasons. You will be redirected to the login page.
            </div>
        `;

        // Clear the page content and show timeout message
        const body = document.body;
        body.innerHTML = '';
        body.appendChild(timeoutDiv);
    }

    showSuccessMessage(message) {
        const successDiv = document.createElement('div');
        successDiv.className = 'alert alert-success session-success';
        successDiv.textContent = message;

        const mainContent = document.querySelector('main') || document.body;
        mainContent.insertBefore(successDiv, mainContent.firstChild);

        setTimeout(() => {
            successDiv.remove();
        }, 3000);
    }

    setupActivityListeners() {
        // Reset timer on user activity
        const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];

        events.forEach(event => {
            document.addEventListener(event, () => {
                // Only reset if user is authenticated
                if (this.isUserAuthenticated()) {
                    this.startSessionTimer();
                }
            }, { passive: true });
        });
    }

    checkForSessionExpiredParam() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get('sessionExpired') === 'true') {
            // Session already expired, don't start timers
            return;
        }
    }

    isUserAuthenticated() {
        // Check if user is authenticated by looking for auth-specific elements
        return document.querySelector('[sec\\:authorize]') !== null ||
               document.querySelector('.auth-card h1') === null ||
               window.location.pathname.includes('/accounts/');
    }
}

// Initialize session manager when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if not on login/register pages and user appears to be authenticated
    if (!window.location.pathname.includes('/login') &&
        !window.location.pathname.includes('/register') &&
        !window.location.pathname.includes('/forgot-password') &&
        !window.location.pathname.includes('/reset-password')) {

        window.sessionManager = new SessionManager();
    }
});

// Handle AJAX responses for session expiration
document.addEventListener('DOMContentLoaded', function() {
    // Intercept all fetch requests to handle session expiration
    const originalFetch = window.fetch;
    window.fetch = function(...args) {
        return originalFetch.apply(this, args)
            .then(response => {
                if (response.status === 401 || response.status === 403) {
                    // Session likely expired
                    if (window.sessionManager) {
                        window.sessionManager.handleSessionTimeout();
                    } else {
                        window.location.href = '/api/v1/auth/login?sessionExpired=true';
                    }
                }
                return response;
            });
    };
});
