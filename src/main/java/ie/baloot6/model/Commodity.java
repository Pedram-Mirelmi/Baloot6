package ie.baloot6.model;


import com.google.gson.annotations.SerializedName;
import ie.baloot6.exception.NotEnoughAmountException;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "commodities")
public class Commodity {

    @Id
    @SerializedName("id")
    private long commodityId;

    @Column(nullable = false)
    @SerializedName("name")
    private String name;

    @Column(nullable = false)
    @SerializedName("providerId")
    private long providerId;

    @Column(nullable = false)
    @SerializedName("price")
    private long price;

    @Column(nullable = false)
    @SerializedName("inStock")
    private long inStock;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "commoditiesCategories",
                joinColumns = @JoinColumn(name = "commodityId"),
                inverseJoinColumns = @JoinColumn(name = "categoryId"))
    private Set<Category> categorySet;


    public Commodity(long commodityId, String name, long providerId, long price, long inStock) {
        this.commodityId = commodityId;
        this.name = name;
        this.providerId = providerId;
        this.price = price;
        this.inStock = inStock;
    }

    public Commodity(Commodity commodity) {
        this(commodity.commodityId, commodity.name, commodity.providerId, commodity.price, commodity.getInStock());
        categorySet = commodity.getCategorySet();
    }

    public Set<Category> getCategorySet() {
        return categorySet;
    }

    public Commodity() {

    }

    public long getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(long commodityId) {
        this.commodityId = commodityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProviderId() {
        return providerId;
    }

    public void setProviderId(long providerId) {
        this.providerId = providerId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getInStock() {
        return inStock;
    }

    public void setInStock(long inStock) {
        this.inStock = inStock;
    }

    public void addStuck(long newAmount) {
        this.inStock += newAmount;
    }

    public void subtractStock(long amount) throws NotEnoughAmountException {
        if(inStock < amount) {
            throw new NotEnoughAmountException("Not enough in stock");
        }
        setInStock(getInStock() - amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commodity commodity = (Commodity) o;
        return commodityId == commodity.commodityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commodityId, name, providerId, price);
    }
}