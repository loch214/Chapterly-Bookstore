// Footer Newsletter Form Handling
document.addEventListener('DOMContentLoaded', function() {
    const newsletterForm = document.querySelector('.newsletter-form');
    if (newsletterForm) {
        newsletterForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const emailInput = this.querySelector('input[type="email"]');
            const email = emailInput.value.trim();

            if (email) {
                // Here you would typically make an API call to your backend
                // For now, we'll just show a success message
                emailInput.value = '';
                showNewsletterMessage('Thank you for subscribing!', 'success');
            } else {
                showNewsletterMessage('Please enter a valid email address.', 'error');
            }
        });
    }
});

// Helper function to show newsletter messages
function showNewsletterMessage(message, type) {
    const container = document.querySelector('.newsletter-form').parentElement;
    const existingMessage = container.querySelector('.newsletter-message');
    
    if (existingMessage) {
        existingMessage.remove();
    }

    const messageElement = document.createElement('div');
    messageElement.className = `newsletter-message ${type === 'success' ? 'text-success' : 'text-danger'} mt-2`;
    messageElement.textContent = message;
    
    container.appendChild(messageElement);

    // Remove message after 3 seconds
    setTimeout(() => {
        messageElement.remove();
    }, 3000);
}

// Social Media Share Functions
function shareOnFacebook() {
    window.open('https://www.facebook.com/sharer/sharer.php?u=' + encodeURIComponent(window.location.href), 
                '_blank', 
                'width=600,height=400');
}

function shareOnTwitter() {
    window.open('https://twitter.com/intent/tweet?url=' + encodeURIComponent(window.location.href) +
                '&text=' + encodeURIComponent('Check out this amazing book at Chapterly!'), 
                '_blank', 
                'width=600,height=400');
}

function shareOnLinkedIn() {
    window.open('https://www.linkedin.com/sharing/share-offsite/?url=' + encodeURIComponent(window.location.href), 
                '_blank', 
                'width=600,height=400');
}

// Add social media click handlers
document.addEventListener('DOMContentLoaded', function() {
    const socialLinks = document.querySelectorAll('.footer-social a');
    socialLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const platform = this.getAttribute('data-platform');
            switch(platform) {
                case 'facebook':
                    e.preventDefault();
                    shareOnFacebook();
                    break;
                case 'twitter':
                    e.preventDefault();
                    shareOnTwitter();
                    break;
                case 'linkedin':
                    e.preventDefault();
                    shareOnLinkedIn();
                    break;
                // For instagram and whatsapp, do not preventDefault
            }
        });
    });
}); 