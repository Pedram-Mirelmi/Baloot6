package ie.baloot6.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    private long ratingId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long commodityId;

    @Column(nullable = false)
    private float rating;

}
