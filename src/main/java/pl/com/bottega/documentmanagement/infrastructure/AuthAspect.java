package pl.com.bottega.documentmanagement.infrastructure;

import org.springframework.stereotype.Component;
import pl.com.bottega.documentmanagement.api.AuthRequiredException;
import pl.com.bottega.documentmanagement.api.RequiresAuth;
import pl.com.bottega.documentmanagement.api.UserManager;

/**
 * Created by paulina.pislewicz on 2016-07-09.
 */
@Component
public class AuthAspect {
    UserManager userManager;

    public AuthAspect(UserManager userManager){
        this.userManager = userManager;
    }
    public void checkAuthentication(RequiresAuth requiresAuth){
        if (!userManager.isAuthenticated(requiresAuth.roles()))
            throw new AuthRequiredException();
    }
}
