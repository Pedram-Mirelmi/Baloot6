package ie.baloot6.DTO;

import ie.baloot6.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Timestamp;

public class CommentDTO{
    private Long commentId;
    private final int usersVote;
    private long userId;
    private Long commodityId;
    private String text;
    private Timestamp date;

    public CommentDTO(Comment comment, int usersVote) {
        this.commentId = comment.getCommentId();
        this.usersVote = usersVote;
        this.userId = comment.getUser().getUserId();
        this.commodityId = comment.getCommodity().getCommodityId();
        this.text = comment.getText();
        this.date = comment.getDate();
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
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

    public void setDate(Timestamp date) {
        this.date = date;
    }



    public int getUsersVote() {
        return usersVote;
    }
}
