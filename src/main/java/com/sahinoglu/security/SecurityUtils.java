package com.sahinoglu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sahinoglu.employee.Employee;

public class SecurityUtils {

    public static Employee getCurrentEmployee() {
        return getCurrentUser().getEmployee();
    }

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (CustomUserDetails) authentication.getPrincipal();
    }

    public static Long getCurrentCenterId() {
        Employee e = getCurrentEmployee();

        if (e.getCenter() == null) {
            return null;
        }

        return e.getCenter().getId();
    }

    public static Long getCurrentBranchId() {
        Employee e = getCurrentEmployee();

        if (e.getBranch() == null) {
            return null;
        }

        return e.getBranch().getId();
    }
}