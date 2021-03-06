package buptspirit.spm.persistence.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "notice", schema = "spm")
@NamedQueries({
        @NamedQuery(name = "notice.findAllWithAuthor",
                query = "SELECT n, t, u FROM NoticeEntity n " +
                        "JOIN TeacherEntity t ON n.author = t.userId " +
                        "JOIN UserInfoEntity u ON u.userId = t.userId " +
                        "ORDER BY n.id DESC"
        )
})
public class NoticeEntity {
    private int noticeId;
    private int author;
    private String title;
    private String detail;
    private Timestamp timeCreated;

    @Id
    @GeneratedValue
    @Column(name = "notice_id", nullable = false)
    public int getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(int noticeId) {
        this.noticeId = noticeId;
    }

    @Basic
    @Column(name = "author", nullable = false)
    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    @Basic
    @Column(name = "title", nullable = false, length = 128)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "detail", nullable = false, length = -1)
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Basic
    @Column(name = "time_created", nullable = false)
    public Timestamp getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoticeEntity that = (NoticeEntity) o;
        return noticeId == that.noticeId &&
                author == that.author &&
                Objects.equals(title, that.title) &&
                Objects.equals(detail, that.detail) &&
                Objects.equals(timeCreated, that.timeCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noticeId, author, title, detail, timeCreated);
    }

    @PrePersist
    protected void prePersist() {
        this.setTimeCreated(new Timestamp(System.currentTimeMillis()));
    }
}
