package ie.baloot6.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.sql.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @Column(unique = true, nullable = false)
    @SerializedName("username")
    private String username;

    @Column(nullable = false)
    @SerializedName("password")
    private String password;

    @Column(nullable = false)
    @JsonIgnore
    private long buyListId;

    @Column(nullable = false)
    @SerializedName("email")
    private String email;

    @Column(nullable = false)
    @SerializedName("birthDate")
    private Date birthDate;

    @Column(nullable = false)
    @SerializedName("address")
    private String address;

    @Column(nullable = false)
    @SerializedName("credit")
    private long credit;


    public User(String username, String password, String email, Date birthDate, String address, long credit) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.credit = credit;
    }

    public User(User user) {
        this(user.username, user.password, user.email, user.birthDate, user.address, user.credit);
    }

    public User() {

    }


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getCredit() {
        return credit;
    }

    public void setCredit(long credit) {
        this.credit = credit;
    }

    public long getBuyListId() {
        return buyListId;
    }

    public void setBuyListId(long buyListId) {
        this.buyListId = buyListId;
    }

}