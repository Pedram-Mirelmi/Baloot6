package ie.baloot6.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "shoppingLists")
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long shoppingListId;

    @ManyToOne
    @JoinColumn(name = "userId")
    @Column(nullable = false)
    private User user;

    @OneToMany
    @JoinColumn(name = "shoppingListId")
    private Set<ShoppingItem> shoppingItemSet;

    public ShoppingList() {
    }

    public ShoppingList(User user) {
        this.user = user;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public Set<ShoppingItem> getShoppingItemSet() {
        return shoppingItemSet;
    }

    public void setShoppingItemSet(Set<ShoppingItem> shoppingItemSet) {
        this.shoppingItemSet = shoppingItemSet;
    }

    public void setShoppingListId(Long id) {
        this.shoppingListId = id;
    }

    public Long getShoppingListId() {
        return shoppingListId;
    }

    public User getUser() {
        return user;
    }
}
