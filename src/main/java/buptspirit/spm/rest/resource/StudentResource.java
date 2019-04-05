package buptspirit.spm.rest.resource;

import buptspirit.spm.exception.ServiceError;
import buptspirit.spm.logic.UserLogic;
import buptspirit.spm.message.SessionMessage;
import buptspirit.spm.message.StudentMessage;
import buptspirit.spm.message.StudentRegisterMessage;
import buptspirit.spm.exception.ServiceException;
import buptspirit.spm.rest.filter.AuthenticatedSession;
import buptspirit.spm.rest.filter.Role;
import buptspirit.spm.rest.filter.Secured;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

@Path("students")
public class StudentResource {

    @Inject
    private UserLogic userLogic;

    @Inject
    @AuthenticatedSession
    private SessionMessage sessionMessage;

    @GET
    @Path("{username}")
    @Secured({Role.Student, Role.Teacher, Role.Administrator})
    @Produces(MediaType.APPLICATION_JSON)
    public StudentMessage getStudent(
            @PathParam("username") String username
    ) throws ServiceException {
        if (sessionMessage.getUserInfo().getRole().equals("student")) {
            if (!sessionMessage.getUserInfo().getUsername().equals(username)) {
                throw ServiceError.GET_STUDENT_NO_PERMISSION.toException();
            }
        }
        return userLogic.getStudent(username);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public StudentMessage register(StudentRegisterMessage registerMessage) throws ServiceException {
        return userLogic.createStudent(registerMessage);
    }
}
