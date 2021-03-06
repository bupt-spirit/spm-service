package buptspirit.spm.logic;

import buptspirit.spm.exception.ServiceAssertionException;
import buptspirit.spm.exception.ServiceError;
import buptspirit.spm.exception.ServiceException;
import buptspirit.spm.message.LoginMessage;
import buptspirit.spm.message.MessageMapper;
import buptspirit.spm.message.StudentMessage;
import buptspirit.spm.message.StudentRegisterMessage;
import buptspirit.spm.message.TeacherMessage;
import buptspirit.spm.message.TeacherRegisterMessage;
import buptspirit.spm.message.UserInfoMessage;
import buptspirit.spm.persistence.entity.StudentEntity;
import buptspirit.spm.persistence.entity.TeacherEntity;
import buptspirit.spm.persistence.entity.UserInfoEntity;
import buptspirit.spm.persistence.facade.StudentFacade;
import buptspirit.spm.persistence.facade.TeacherFacade;
import buptspirit.spm.persistence.facade.UserInfoFacade;
import buptspirit.spm.rest.filter.Role;
import buptspirit.spm.utility.PasswordHash;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import static buptspirit.spm.exception.ServiceAssertionUtility.serviceAssert;
import static buptspirit.spm.persistence.JpaUtility.transactional;

@Singleton
public class UserLogic {

    private static final String DEFAULT_ADMIN_PASSWORD = "bupt-spirit";

    @Inject
    private UserInfoFacade userInfoFacade;

    @Inject
    private StudentFacade studentFacade;

    @Inject
    private PasswordHash passwordHash;

    @Inject
    private Logger logger;

    @Inject
    private TeacherFacade teacherFacade;

    @Inject
    private MessageMapper messageMapper;

    @PostConstruct
    public void postConstruct() {
        logger.trace("successfully constructed");

        // ensure an administrator exists
        if (!isAdministratorExists()) {
            createAdministrator();
        }
    }

    // return null if failed to verify user
    UserInfoMessage verify(LoginMessage loginMessage) throws ServiceAssertionException {
        loginMessage.enforce();

        UserInfoEntity info = transactional(
                em -> userInfoFacade.findByUsername(em, loginMessage.getUsername()),
                "failed to find user by username"
        );
        if (info != null && passwordHash.verify(loginMessage.getPassword().toCharArray(), info.getPassword())) {
            return UserInfoMessage.fromEntity(info);
        } else {
            return null;
        }
    }

    // TODO remote duplicates with createTeacher
    @SuppressWarnings("Duplicates")
    public StudentMessage createStudent(StudentRegisterMessage registerMessage) throws ServiceException, ServiceAssertionException {
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
        newUser.setRole(Role.Student.getName());
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

        UserInfoMessage userInfoMessage = UserInfoMessage.fromEntity(newUser);
        return StudentMessage.fromEntity(newStudent, userInfoMessage);
    }

    public StudentMessage getStudent(String username) throws ServiceException, ServiceAssertionException {
        serviceAssert(username != null && !username.isEmpty());

        StudentMessage message = transactional(
                em -> {
                    UserInfoEntity user = userInfoFacade.findByUsername(em, username);
                    if (user == null)
                        return null;
                    StudentEntity student = studentFacade.find(em, user.getUserId());
                    if (student == null)
                        return null;
                    return StudentMessage.fromEntity(student, UserInfoMessage.fromEntity(user));
                },
                "failed to find student"
        );
        if (message == null)
            throw ServiceError.GET_STUDENT_NO_SUCH_STUDENT.toException();
        return message;
    }

    public TeacherMessage getTeacher(String username) throws ServiceException, ServiceAssertionException {
        serviceAssert(username != null && !username.isEmpty());

        TeacherMessage message = transactional(
                em -> {
                    UserInfoEntity user = userInfoFacade.findByUsername(em, username);
                    if (user == null)
                        return null;
                    TeacherEntity teacher = teacherFacade.find(em, user.getUserId());
                    if (teacher == null)
                        return null;
                    return TeacherMessage.fromEntity(teacher, UserInfoMessage.fromEntity(user));
                },
                "failed to find teacher"
        );
        if (message == null)
            throw ServiceError.GET_TEACHER_NO_SUCH_TEACHER.toException();
        return message;
    }

    @SuppressWarnings("Duplicates")
    public TeacherMessage createTeacher(TeacherRegisterMessage registerMessage) throws ServiceException, ServiceAssertionException {
        registerMessage.enforce();

        boolean exists = transactional(
                em -> userInfoFacade.findByUsername(em, registerMessage.getUsername()) != null,
                "failed to find user by name"
        );
        if (exists)
            throw ServiceError.POST_TEACHER_USERNAME_ALREADY_EXISTS.toException();
        UserInfoEntity newUser = new UserInfoEntity();
        newUser.setUsername(registerMessage.getUsername());
        newUser.setPassword(passwordHash.generate(registerMessage.getPassword().toCharArray()));
        newUser.setRole(Role.Teacher.getName());
        newUser.setRealName(registerMessage.getRealName());
        newUser.setEmail(registerMessage.getEmail());
        newUser.setPhone(registerMessage.getPhone());
        TeacherEntity newTeacher = new TeacherEntity();
        newTeacher.setIntroduction(registerMessage.getIntroduction());
        transactional(
                em -> {
                    userInfoFacade.create(em, newUser);
                    logger.debug("user id={} created", newUser.getUserId());
                    newTeacher.setUserId(newUser.getUserId());
                    teacherFacade.create(em, newTeacher);
                    return null;
                },
                "failed to create user"
        );

        UserInfoMessage userInfoMessage = UserInfoMessage.fromEntity(newUser);
        return TeacherMessage.fromEntity(newTeacher, userInfoMessage);
    }

    @SuppressWarnings("Duplicates")
    private boolean isAdministratorExists() {
        UserInfoEntity admin = transactional(
                em -> userInfoFacade.findByUsername(em, "0000000000"),
                "failed to create user"
        );
        return admin != null;
    }

    @SuppressWarnings("Duplicates")
    private void createAdministrator() {
        UserInfoEntity newUser = new UserInfoEntity();
        newUser.setUsername("0000000000");
        newUser.setPassword(passwordHash.generate(DEFAULT_ADMIN_PASSWORD.toCharArray()));
        newUser.setRole(Role.Administrator.getName());
        newUser.setRealName("管理员");
        newUser.setEmail("");
        newUser.setPhone("");
        transactional(
                em -> {
                    userInfoFacade.create(em, newUser);
                    logger.debug("user id={} created", newUser.getUserId());
                    return null;
                },
                "failed to create user"
        );
    }

    public List<TeacherMessage> getAllTeachers() throws ServiceException {
        List<TeacherMessage> messages = transactional(
                em -> {
                    List<TeacherEntity> teachers = teacherFacade.findAll(em);
                    return teachers.stream().map(
                            teacher -> messageMapper.intoTeacherMessage(em, teacher)
                    ).collect(Collectors.toList());
                },
                "failed to find teacher"
        );
        if (messages == null)
            throw ServiceError.GET_TEACHER_NO_SUCH_TEACHER.toException();
        return messages;
    }
}
