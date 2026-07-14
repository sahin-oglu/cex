package com.sahinoglu.ui;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication, Model model) {

        boolean loggedIn =
                authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        String currentUser = loggedIn
                ? authentication.getName()
                : "Not logged in";

        model.addAttribute("currentUser", currentUser);

        return "login";
    }
}