package com.josephhieu.vaccinebackend.common.utils;

import com.josephhieu.vaccinebackend.modules.auth.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.UUID;

public class SecurityUtils {
    public static UUID getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getTaiKhoan().getMaTaiKhoan();
        }
        return null;
    }
}