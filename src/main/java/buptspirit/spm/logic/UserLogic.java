package buptspirit.spm.logic;

import buptspirit.spm.exception.ServiceError;
import buptspirit.spm.exception.ServiceException;
import buptspirit.spm.message.LoginMessage;
import buptspirit.spm.message.StudentMessage;
import buptspirit.spm.message.StudentRegisterMessage;
import buptspirit.spm.message.UserInfoMessage;
import buptspirit.spm.password.PasswordHash;
import buptspirit.spm.persistence.entity.StudentEntity;
import buptspirit.spm.persistence.entity.UserInfoEntity;
import buptspirit.spm.persistence.facade.StudentFacade;
import buptspirit.spm.persistence.facade.UserInfoFacade;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import static buptspirit.spm.persistence.JpaUtility.transactional;

@Singleton
public class UserLogic {

    @Inject
    private UserInfoFacade userInfoFacade;

    @Inject
    private StudentFacade studentFacade;

    @Inject
    private PasswordHash passwordHash;

    @Inject
    private Logger logger;

    @PostConstruct
    public void postConstruct() {
        logger.info("successfully constructed");
    }

    // return null if failed to verify user
    UserInfoMessage verify(LoginMessage loginMessage) throws ServiceException {
        loginMessage.enforce();

        UserInfoEntity info = transactional(
                em -> userInfoFacade.findByUsername(em, loginMessage.getUsername()),
                "failed to find user by username"
        );
        if (info != null && passwordHash.verify(loginMessage.getPassword().toCharArray(), info.getPassword())) {
            return UserInfoMessage.FromEntity(info);
        } else {
            return null;
        }
    }

    public StudentMessage createStudent(StudentRegisterMessage registerMessage) throws ServiceException {
        registerMessage.enforce();

        boolean exists = transactional(
                em -> userInfoFacade.findByUsername(em, registerMessage.getUsername()) != null,
                "failed to find user by name"
        );
        if (exists)
            throw ServiceError.POST_STUDENT_USERNAME_ALREADY_EXISTS.toException();
        UserInfoEntity newUser = new UserInfoEntity();
        newUser.setUsername(registerMessage.getUsername());
        newUser.setPassword(passwordHash.generate(registerMessage.getPassword().toCharArray()));
        newUser.setRole("student");
        newUser.setRealName(registerMessage.getRealName());
        newUser.setEmail(registerMessage.getEmail());
        newUser.setPhone(registerMessage.getPhone());
        StudentEntity newStudent = new StudentEntity();
        newStudent.setClazz(registerMessage.getClazz());
        newStudent.setCollege(registerMessage.getCollege());
        newStudent.setNickname(registerMessage.getNickname());
        transactional(
                em -> {
                    userInfoFacade.create(em, newUser);
                    logger.debug("user id={} created", newUser.getUserId());
                    newStudent.setUserId(newUser.getUserId());
                    studentFacade.create(em, newStudent);
                    return null;
                },
                "failed to create user"
        );


        UserInfoMessage userInfoMessage = UserInfoMessage.FromEntity(newUser);
        return StudentMessage.FromEntity(newStudent, userInfoMessage);
    }

    public StudentMessage getStudent(String username) throws ServiceException {
        if (username != null && username.isEmpty()) {
            throw ServiceError.GET_STUDENT_USERNAME_IS_EMPTY.toException();
        }

        StudentMessage message = transactional(
                em -> {
                    UserInfoEntity user = userInfoFacade.findByUsername(em, username);
                    if (user == null)
                        return null;
                    StudentEntity student = studentFacade.find(em, user.getUserId());
                    if (student == null)
                        return null;
                    return StudentMessage.FromEntity(student, UserInfoMessage.FromEntity(user));
                },
                "failed to find student"
        );
        if (message == null)
            throw ServiceError.GET_STUDENT_NO_SUCH_STUDENT.toException();
        return message;
    }
}
