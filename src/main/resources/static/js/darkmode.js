// Dark mode toggle functionality
function setDarkMode(enabled) {
    if (enabled) {
        document.body.classList.add('dark-mode');
        document.documentElement.classList.add('dark-mode');
        localStorage.setItem('darkMode', 'true');
    } else {
        document.body.classList.remove('dark-mode');
        document.documentElement.classList.remove('dark-mode');
        localStorage.setItem('darkMode', 'false');
    }
}

function toggleDarkMode() {
    const isDark = document.body.classList.contains('dark-mode');
    setDarkMode(!isDark);
}

document.addEventListener('DOMContentLoaded', function() {
    // Dark mode toggle event listeners
    const darkModeToggle = document.getElementById('darkModeToggle');
    const darkModeToggleDesktop = document.getElementById('darkModeToggleDesktop');
    if (darkModeToggle) darkModeToggle.addEventListener('click', toggleDarkMode);
    if (darkModeToggleDesktop) darkModeToggleDesktop.addEventListener('click', toggleDarkMode);
    // Set initial dark mode state from localStorage
    if (localStorage.getItem('darkMode') === 'true') {
        document.body.classList.add('dark-mode');
        document.documentElement.classList.add('dark-mode');
    }
}); 