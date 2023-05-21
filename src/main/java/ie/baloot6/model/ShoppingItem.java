package ie.baloot6.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "shoppingItems")
public class ShoppingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long shoppingItemId;


    @ManyToOne
    @JoinColumn(name = "commodityId", nullable = false)
    private Commodity commodity;

    @ManyToOne
    @JoinColumn(name = "shoppingListId")
    private ShoppingList shoppingList;

    @Column(nullable = false)
    private long count;

    public ShoppingItem() {

    }

    public ShoppingItem(Commodity commodity, ShoppingList shoppingList, long count) {
        this.commodity = commodity;
        this.shoppingList = shoppingList;
        this.count = count;
    }

    public ShoppingItem(Commodity commodity, long count) {
        this.commodity = commodity;
        this.count = count;
    }

    public long getShoppingItemId() {
        return shoppingItemId;
    }

    public void setShoppingItemId(long shoppingItemId) {
        this.shoppingItemId = shoppingItemId;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
