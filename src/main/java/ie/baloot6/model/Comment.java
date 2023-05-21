package ie.baloot6.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @Column(nullable = false)
    private long userId;


    @Column(nullable = false)
    private Long commodityId;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Timestamp date;

    public Comment(long userId, long commodityId, String text) {
        this.userId = userId;
        this.commentId = commodityId;
        this.text = text;
    }

    public Comment() {
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getDate() {
        return date;
    }

    public Long getCommodityId() {
        return commodityId;
    }
}