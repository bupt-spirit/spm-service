package buptspirit.spm.rest.resource;

import buptspirit.spm.exception.ServiceAssertionException;
import buptspirit.spm.exception.ServiceException;
import buptspirit.spm.logic.CourseLogic;
import buptspirit.spm.message.CourseCreationMessage;
import buptspirit.spm.message.SessionMessage;
import buptspirit.spm.rest.filter.AuthenticatedSession;
import buptspirit.spm.rest.filter.Role;
import buptspirit.spm.rest.filter.Secured;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("course")
public class CourseResource {

    @Inject
    @AuthenticatedSession
    private SessionMessage sessionMessage;

    @Inject
    private CourseLogic courseLogic;


    @Inject
    private Logger logger;

    @POST
    @Secured({Role.Teacher})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CourseCreationMessage createCourse(CourseCreationMessage courseCreationMessage) throws ServiceAssertionException {
        System.out.print(courseCreationMessage.getStartDate());
        return courseLogic.createCourse(courseCreationMessage);
    }

    @GET
    @Secured({Role.Administrator,Role.Teacher,Role.Student})
    @Produces(MediaType.APPLICATION_JSON)
    public List<CourseCreationMessage> getAllCourses() throws ServiceException, ServiceAssertionException {
        return courseLogic.getAllCourses();
    }

}
