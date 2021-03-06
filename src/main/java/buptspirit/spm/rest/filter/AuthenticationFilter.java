package buptspirit.spm.rest.filter;

import buptspirit.spm.logic.SessionLogic;
import buptspirit.spm.message.SessionMessage;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    @Context
    UriInfo uriInfo;

    @Inject
    @AuthenticatedSession
    Event<SessionMessage> userAuthenticatedEvent;

    @Inject
    private SessionLogic sessionLogic;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        SessionMessage session = SessionMessage.Unauthenticated();
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer".length()).trim();
            session = sessionLogic.getSessionFromToken(token);

            requestContext.setSecurityContext(
                    new SecurityContextImpl(
                            session.getUserInfo().getUsername(),
                            session.getUserInfo().getRole(),
                            uriInfo.getAbsolutePath().toString().startsWith("https"),
                            "BEARER"
                    )
            );
        }
        userAuthenticatedEvent.fire(session);
    }
}

class SecurityContextImpl implements SecurityContext {
    private final Principal principal;
    private final String role;
    private final boolean secure;
    private final String authenticationScheme;

    @SuppressWarnings("SameParameterValue")
    SecurityContextImpl(String username, String role, boolean secure, String authenticationScheme) {
        this.principal = new PrincipalImpl(username);
        this.role = role;
        this.secure = secure;
        this.authenticationScheme = authenticationScheme;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.role.equals(role);
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public String getAuthenticationScheme() {
        return this.authenticationScheme;
    }
}

class PrincipalImpl implements Principal {
    private final String username;

    PrincipalImpl(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
