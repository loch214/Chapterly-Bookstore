package com.bookstore.onlinebookstore.config;

import com.bookstore.onlinebookstore.model.User;
import com.bookstore.onlinebookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    @Lazy
    private UserService userService;

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        User user = userService.findByUsernameOrEmail(username);
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // Check for pending action from before login
        String pendingAction = (String) session.getAttribute("pendingAction");
        if (pendingAction != null) {
            handlePendingAction(request, response, session, pendingAction);
            return;
        }

        // Check if there's a saved request from Spring Security
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            // If the target is an API endpoint, redirect to home instead
            if (targetUrl.endsWith("/cart/count") || targetUrl.endsWith("/wishlist/count")) {
                response.sendRedirect("/");
                return;
            }
            requestCache.removeRequest(request, response);
            response.sendRedirect(targetUrl);
            return;
        }

        // Default redirect to home page
        response.sendRedirect("/");
    }

    private void handlePendingAction(HttpServletRequest request, HttpServletResponse response, HttpSession session, String pendingAction) throws IOException {
        String pendingBookId = (String) session.getAttribute("pendingBookId");
        String pendingQuantity = (String) session.getAttribute("pendingQuantity");
        String pendingCurrentPage = (String) session.getAttribute("pendingCurrentPage");

        // Clear pending action attributes from session
        session.removeAttribute("pendingAction");
        session.removeAttribute("pendingBookId");
        session.removeAttribute("pendingQuantity");
        session.removeAttribute("pendingCurrentPage");

        if ("addToCart".equals(pendingAction)) {
            // Redirect back to the original page with parameters to trigger the add to cart action
            String referer = pendingCurrentPage != null && !pendingCurrentPage.isEmpty() ? pendingCurrentPage : request.getHeader("Referer");
            if (referer != null && !referer.contains("/login")) {
                String separator = referer.contains("?") ? "&" : "?";
                String redirectUrl = referer + separator + "addToCart=true&bookId=" + pendingBookId + "&quantity=" + pendingQuantity;
                response.sendRedirect(redirectUrl);
                return;
            }
        } else if ("buyNow".equals(pendingAction)) {
            // For buy now, redirect to cart with buyNow parameter
            String redirectUrl = "/cart?buyNow=true&bookId=" + pendingBookId + "&quantity=" + pendingQuantity;
            response.sendRedirect(redirectUrl);
            return;
        }

        // Fallback redirect if action handling is not specific
        response.sendRedirect("/");
    }
} 