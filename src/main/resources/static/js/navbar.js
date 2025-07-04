// Smart auto-hide/reveal navbar on scroll
(function() {
    function initSmartNavbar() {
        const navbar = document.querySelector('.navbar');
        if (!navbar) {
            return;
        }

        let lastScrollY = window.scrollY;
        let ticking = false;
        let navbarHeight = navbar.offsetHeight;

        function onScroll() {
            const currentScrollY = window.scrollY;
            if (currentScrollY <= 0) {
                navbar.classList.remove('navbar-hidden');
                navbar.classList.add('navbar-visible');
            } else if (currentScrollY > lastScrollY && currentScrollY > navbarHeight) {
                // Scrolling down, hide navbar only if fully out of view
                navbar.classList.add('navbar-hidden');
                navbar.classList.remove('navbar-visible');
            } else if (currentScrollY + 5 < lastScrollY) {
                // Scrolling up, show navbar
                navbar.classList.remove('navbar-hidden');
                navbar.classList.add('navbar-visible');
            }
            lastScrollY = currentScrollY;
            ticking = false;
        }

        function requestTick() {
            if (!ticking) {
                window.requestAnimationFrame(onScroll);
                ticking = true;
            }
        }

        window.addEventListener('scroll', requestTick, { passive: true });
        // Show navbar on load
        navbar.classList.add('navbar-visible');
    }
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initSmartNavbar);
    } else {
        initSmartNavbar();
    }
})(); 