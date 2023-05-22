package ie.baloot6.DTO;

import ie.baloot6.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.sql.Date;
import java.sql.Timestamp;

public class CommentDTO {
    private Long commentId;
    private String username;
    private String userEmail;
    private Long commodityId;
    private String text;
    private Date date;

    private long likes;
    private long dislikes;
    private final int usersVote;

    public CommentDTO(Comment comment, int usersVote) {
        this.commentId = comment.getCommentId();
        this.username = comment.getUser().getUsername();
        this.userEmail = comment.getUser().getEmail();
        this.commodityId = comment.getCommodity().getCommodityId();
        this.text = comment.getText();
        this.date = comment.getDate();
        this.usersVote = usersVote;
        comment.getVotes().forEach(v -> {
            if(v.getVote() == 1) {
                likes++;
            }
            if(v.getVote() == -1) {
                dislikes++;
            }
        });
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getUsersVote() {
        return usersVote;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }
}
