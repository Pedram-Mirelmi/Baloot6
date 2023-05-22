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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "providerId")
    private Provider provider;

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


    public Commodity(String name, Provider provider, long price, long inStock) {
        this.provider = provider;
        this.name = name;
        this.price = price;
        this.inStock = inStock;
    }

    public Commodity(Commodity commodity) {
        this(commodity.name, commodity.getProvider(), commodity.price, commodity.getInStock());
        this.commodityId = commodity.getCommodityId();
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

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Commodity commodity = (Commodity) o;
        return commodityId == commodity.commodityId;
    }
}