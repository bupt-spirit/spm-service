package buptspirit.spm.rest.resource;

import buptspirit.spm.exception.ServiceAssertionException;
import buptspirit.spm.exception.ServiceError;
import buptspirit.spm.exception.ServiceException;
import buptspirit.spm.logic.ChapterLogic;
import buptspirit.spm.logic.CourseLogic;
import buptspirit.spm.logic.SectionLogic;
import buptspirit.spm.logic.SessionLogic;
import buptspirit.spm.logic.UserLogic;
import buptspirit.spm.message.ChapterCreationMessage;
import buptspirit.spm.message.ChapterMessage;
import buptspirit.spm.message.CourseCreationMessage;
import buptspirit.spm.message.CourseMessage;
import buptspirit.spm.message.LoginMessage;
import buptspirit.spm.message.SectionCreationMessage;
import buptspirit.spm.message.SessionMessage;
import buptspirit.spm.message.StudentRegisterMessage;
import buptspirit.spm.message.TeacherRegisterMessage;
import buptspirit.spm.rest.filter.AuthenticatedSession;
import buptspirit.spm.rest.filter.Role;
import buptspirit.spm.rest.filter.Secured;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("meta")
public class MetaResource {

    private static final String DEFAULT_MOCK_PASSWORD = "bupt-spirit";

    @Inject
    private SessionLogic sessionLogic;

    @Inject
    private UserLogic userLogic;

    @Inject
    private CourseLogic courseLogic;

    @Inject
    private ChapterLogic chapterLogic;

    @Inject
    private SectionLogic sectionLogic;

    @Inject
    @AuthenticatedSession
    private SessionMessage sessionMessage;

    @GET
    @Path("errors")
    @Produces(MediaType.TEXT_PLAIN)
    public String getErrors() {
        StringBuilder builder = new StringBuilder();
        builder.append("enum ErrorCode {\n");
        for (ServiceError error : ServiceError.values()) {
            builder.append("    ");
            builder.append(error.name());
            builder.append(" = ");
            builder.append(error.getCode());
            builder.append(",\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

    @SuppressWarnings("SameReturnValue")
    @GET
    @Path("is-working")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean getWorkingStatus() {
        return true;
    }

    @POST
    @Path("mock/users")
    @Secured({Role.Administrator})
    public Response createUserMockData() throws ServiceException, ServiceAssertionException {

        TeacherRegisterMessage teacher1 = new TeacherRegisterMessage();
        teacher1.setUsername("1000000001");
        teacher1.setEmail("han.wanjiang@bupt.edu.cn");
        teacher1.setPhone("10010010010");
        teacher1.setPassword(DEFAULT_MOCK_PASSWORD);
        teacher1.setRealName("韩万江");
        teacher1.setIntroduction("北京邮电大学软件学院副教授。在软件开发、项目管理、质量保证等研究领域积累了丰富的软件开发经验，尤其是在项目管理和软件工程方面具有大量的实践经验。");
        userLogic.createTeacher(teacher1);

        TeacherRegisterMessage teacher2 = new TeacherRegisterMessage();
        teacher2.setUsername("1000000002");
        teacher2.setEmail("zhang.xiaoyan@bupt.edu.cn");
        teacher2.setPhone("10010010010");
        teacher2.setPassword(DEFAULT_MOCK_PASSWORD);
        teacher2.setRealName("张笑燕");
        teacher2.setIntroduction("博士，教授，硕士生导师，北京邮电大学软件学院副院长兼党委副书记。主讲个体软件开发过程（PSP）、小组软件开发过程（TSP）、现代通信网络、计算机网络、操作系统等课程。");
        userLogic.createTeacher(teacher2);

        StudentRegisterMessage student1 = new StudentRegisterMessage();
        student1.setUsername("2000000001");
        student1.setClazz("2016211505");
        student1.setEmail("sun.hao@bupt.edu.cn");
        student1.setCollege("BUPT");
        student1.setPassword(DEFAULT_MOCK_PASSWORD);
        student1.setNickname("浩");
        student1.setRealName("孙浩");
        student1.setPhone("13300000000");
        userLogic.createStudent(student1);

        return Response.noContent().build();
    }

    @POST
    @Path("mock/courses")
    @Secured({Role.Administrator})
    public Response createCourseMockData() throws ServiceException, ServiceAssertionException {

        // login as teacher
        LoginMessage teacherLoginMessage = new LoginMessage();
        teacherLoginMessage.setUsername("1000000001");
        teacherLoginMessage.setPassword("bupt-spirit");
        SessionMessage teacherSessionMessage = sessionLogic.createSession(teacherLoginMessage);

        CourseCreationMessage course1 = new CourseCreationMessage();
        course1.setCourseName("软件项目管理");
        course1.setDescription("《软件项目管理》是软件工程专业的专业课程，本课程有贯穿始终的项目案例， 让学生切身体会软件项目管理过程，本课程于2007年度获得教育部-IBM精品课程, 教材《软件项目管理案例教程》是北京市精品教材，十一五国家规划教教材，有百余所高校采用，具有不错口碑。");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos1 = new ParsePosition(0);
        ParsePosition pos2 = new ParsePosition(0);
        Date startDate1 = formatter.parse("2019-03-04", pos1);
        course1.setStartDate(new java.sql.Date(startDate1.getTime()));
        Date finishDate1 = formatter.parse("2019-07-07", pos2);
        course1.setFinishDate(new java.sql.Date(finishDate1.getTime()));
        course1.setPeriod((byte) 32);
        CourseMessage courseMessage = courseLogic.createCourse(teacherSessionMessage, course1);
        int courseId = courseMessage.getCourseId();

        CourseCreationMessage course2 = new CourseCreationMessage();
        course2.setCourseName("软件项目管理");
        course2.setDescription("《软件项目管理》是软件工程专业的专业课程，本课程有贯穿始终的项目案例， 让学生切身体会软件项目管理过程，本课程于2007年度获得教育部-IBM精品课程, 教材《软件项目管理案例教程》是北京市精品教材，十一五国家规划教教材，有百余所高校采用，具有不错口碑。");
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos3 = new ParsePosition(0);
        ParsePosition pos4 = new ParsePosition(0);
        Date startDate2 = formatter1.parse("2018-12-25", pos3);
        course2.setStartDate(new java.sql.Date(startDate2.getTime()));
        Date finishDate2 = formatter1.parse("2019-02-25", pos4);
        course2.setFinishDate(new java.sql.Date(finishDate2.getTime()));
        course2.setPeriod((byte) 32);
        courseLogic.createCourse(teacherSessionMessage, course2);

        String[] chapterName = {"软件项目管理基本概念", "软件项目确立", "生存期模型",
                "软件项目需求管理", "软件项目任务分解", "软件项目成本计划", "软件项目进度计划",
                "软件项目质量计划", "软件项目配置管理计划", "软件项目团队计划", "软件项目风险计划",
                "软件项目合同计划", "项目集成计划执行控制", "项目核心计划执行控制", "项目辅助计划执行控制",
                "项目结束过程"};
        String[][] sectionName = {
                {"软件项目管理 基本概念", "PMBOK 与软件项目管理知识体系", "敏捷项目管理", "课件及介绍"},
                {"项目立项", "项目招投标流程", "项目章程", "项目案例", "课件及介绍"},
                {"生存期模型选择", "预测生存期模型", "迭代生存期模型", "增量生存期模型", "敏捷生存期模型", "项目案例", "课件及介绍"},
                {"软件需求管理过程", "传统需求建模方法", "敏捷需求建模方法", "项目案例", "课件及介绍"},
                {"任务分解基本概念", "任务分解方法", "敏捷任务分解", "项目案例", "课件及介绍"},
                {"代码行估算法", "功能点估算法", "用例点估算法", "类比 (自顶向下)估算法", "自下而上估算法", "三点估算法", "参数估算法", "专家估算法", "敏捷估算法", "成本预算", "项目案例", "课件及介绍"},
                {"进度基本知识", "传统历时估算", "敏捷历时估算", "进度计划编排 - 超前与滞后方法", "进度编排方法 - 关键路径法", "进度编排方法 - 时间压缩法", "进度编排方法 - 资源优化", "进度编排方法 - 敏捷计划", "项目进度模型(SPSP)", "项目案例", "课件及介绍", "敏捷规划综述"},
                {"软件质量基本概念", "软件项目质量活动", "敏捷项目质量活动", "软件项目质量计划", "项目案例", "课件及介绍"},
                {"软件配置管理基本概念", "软件项目配置管理过程", "敏捷配置管理计划", "项目案例", "课件及介绍"},
                {"团队计划", "敏捷团队计划", "项目案例", "课件及介绍"},
                {"风险管理过程", "风险管理计划", "项目案例", "课件及介绍"},
                {"项目合同类型", "项目合同计划", "项目案例", "课件及介绍"},
                {"集成计划执行控制", "项目案例", "课件及介绍"},
                {"软件项目范围管理 - 传统与敏捷", "成本进度管理 - 图解控制法", "成本进度管理 - 挣值分析法", "成本进度管理 - 网络图分析", "成本进度管理 - 敏捷方法", "质量管理 - 传统与敏捷", "项目案例", "课件及介绍"},
                {"软件项目辅助计划执行控制 - 传统项目", "软件项目辅助计划执行控制 - 敏捷项目", "项目案例", "课件及介绍"},
                {"项目结束过程", "项目案例", "课件及介绍"}};

        ChapterMessage[] chapterMessages = new ChapterMessage[chapterName.length];

        for (int i = 0; i < chapterName.length; i++) {
            ChapterCreationMessage chapter = new ChapterCreationMessage();
            chapter.setChapterName(chapterName[i]);
            chapter.setSequence((byte) i);
            ChapterMessage chapterMessage = chapterLogic.insertChapter(courseId, chapter, teacherSessionMessage);
            chapterMessages[i] = chapterMessage;
        }

        for (int i = 0; i < chapterName.length; i++) {
            for (int j = 0; j < sectionName[i].length; j++) {
                SectionCreationMessage section = new SectionCreationMessage();
                section.setSectionName(sectionName[i][j]);
                section.setSequence((byte) j);
                sectionLogic.insertSection(chapterMessages[i].getCourseId(), chapterMessages[i].getSequence(), section, teacherSessionMessage);
            }
        }

        return Response.noContent().build();
    }
}
