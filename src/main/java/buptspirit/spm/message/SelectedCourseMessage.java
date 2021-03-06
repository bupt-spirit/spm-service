package buptspirit.spm.message;

import buptspirit.spm.persistence.entity.SelectedCourseEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SelectedCourseMessage {

    private StudentMessage student;
    private CourseSummaryMessage course;
    private Timestamp timeApproved;
    private BigDecimal avgOnlineScore;
    private BigDecimal midScore;
    private BigDecimal finalScore;
    private BigDecimal totalScore;

    public static SelectedCourseMessage fromEntity(SelectedCourseEntity entity, StudentMessage student, CourseSummaryMessage course) {
        SelectedCourseMessage message = new SelectedCourseMessage();
        message.setStudent(student);
        message.setCourse(course);
        message.setApproveTime(entity.getTimeApproved());
        message.setAvgOnlineScore(entity.getAvgOnlineScore());
        message.setMidScore(entity.getMidScore());
        message.setFinalScore(entity.getFinalScore());
        message.setTotalScore(entity.getTotalScore());
        return message;
    }

    public StudentMessage getStudent() {
        return student;
    }

    public void setStudent(StudentMessage student) {
        this.student = student;
    }

    public Timestamp getTimeApproved() {
        return timeApproved;
    }

    public void setTimeApproved(Timestamp timeApproved) {
        this.timeApproved = timeApproved;
    }

    public CourseSummaryMessage getCourse() {
        return course;
    }

    public void setCourse(CourseSummaryMessage course) {
        this.course = course;
    }

    public Timestamp getApproveTime() {
        return timeApproved;
    }

    public void setApproveTime(Timestamp timeApproved) {
        this.timeApproved = timeApproved;
    }

    public BigDecimal getAvgOnlineScore() {
        return avgOnlineScore;
    }

    public void setAvgOnlineScore(BigDecimal avgOnlineScore) {
        this.avgOnlineScore = avgOnlineScore;
    }

    public BigDecimal getMidScore() {
        return midScore;
    }

    public void setMidScore(BigDecimal midScore) {
        this.midScore = midScore;
    }

    public BigDecimal getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }
}

