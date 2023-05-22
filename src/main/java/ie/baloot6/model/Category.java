package ie.baloot6.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "commoditiesCategories",
                joinColumns = @JoinColumn(name = "categoryId"),
                inverseJoinColumns = @JoinColumn(name = "commodityId"))
    private Set<Commodity> commoditySet;


    public long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Set<Commodity> getCommoditySet() {
        return commoditySet;
    }
}
