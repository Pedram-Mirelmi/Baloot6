package ie.baloot6.service;

import ie.baloot6.data.IRepository;
import ie.baloot6.exception.InvalidIdException;
import ie.baloot6.exception.InvalidValueException;
import ie.baloot6.exception.NotEnoughAmountException;
import ie.baloot6.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class Repository implements IRepository {

    private final EntityManagerFactory entityManagerFactory;

    public Repository() {
        var registry = new StandardServiceRegistryBuilder().configure().build();
        entityManagerFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Override
    public void getData(@NotNull String apiUri) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
    }

    @Override
    public void addComment(@NotNull String username, long commodityId, @NotNull String commentText) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Optional<User> user = getUser(username, entityManager);
        if(user.isEmpty())
            throw new InvalidIdException("Invalid username");
        entityManager.persist(new Comment(user.get().getUserId(), commodityId, commentText));

        entityManager.getTransaction().commit();
    }

    @Override
    public void addUser(String username, String password, String email, Date birthDate, String address, long credit) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = new User(username, password, email, birthDate, address, credit);
        ShoppingList buyList = new ShoppingList(user);
        entityManager.persist(buyList);

        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<User> getUser(@NotNull String username) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return getUser(username, entityManager);
    }

    public Optional<User> getUser(@NotNull String username, EntityManager entityManager) {
        List users = entityManager.createQuery("SELECT u FROM User u where u.username=:username")
                .setParameter("username", username).getResultList();
        return users.size() == 1
                ? Optional.of((User) users.get(0))
                : Optional.empty();
    }

    @Override
    public void addProvider(@NotNull String name, long providerId) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = new Provider(name, Date.valueOf(LocalDate.now()));
        provider.setProviderId(providerId);
        entityManager.persist(new Provider());

        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<Provider> getProvider(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = entityManager.find(Provider.class, id);

        entityManager.getTransaction().commit();

        return Optional.ofNullable(provider);
    }

    @Override
    public void addCommodity(long commodityId, String name, long providerId, long price, long inStock) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(new Commodity(commodityId, name, providerId, price, inStock));

        entityManager.getTransaction().commit();
    }

    @Override
    public void addDiscount(@NotNull String discountCode, int discountAmount) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(new Discount(discountCode, discountAmount));

        entityManager.getTransaction().commit();
    }

    @Override
    public List<Commodity> getCommodityList() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var resultList = entityManager.createQuery("SELECT c from Commodity c").getResultList();

        entityManager.getTransaction().commit();
        return resultList;
    }

    @Override
    public List<Commodity> getProvidersCommoditiesList(long providerId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var resultList = entityManager.createQuery("SELECT c from Commodity c where c.providerId=:providerId")
                .setParameter("providerId", providerId)
                .getResultList();

        entityManager.getTransaction().commit();

        return resultList;
    }

    @Override
    public Optional<Commodity> getCommodityById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var commodity = entityManager.find(Commodity.class, id);


        entityManager.getTransaction().commit();
        return Optional.ofNullable(commodity);
    }

    @Override
    public List<Commodity> getCommoditiesByCategory(@NotNull String categoryName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();


        var resultList = entityManager.createQuery("select c from Category c where c.categoryName=:name")
                .setParameter("name", categoryName)
                .getResultList();

        if(resultList.isEmpty())
            return new ArrayList<>();

        Category category = (Category) resultList.get(0);

        List<Commodity> commodityList = category.getCommoditySet().stream().toList();

        entityManager.getTransaction().commit();
        return commodityList;
    }

    @Override
    public List<Commodity> getCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name=:name")
                .setParameter("name", name)
                .getResultList();

        entityManager.getTransaction().commit();
        return resultList;
    }

    @Override
    public List<Commodity> searchCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name like :name")
                .setParameter("name", name)
                .getResultList();

        entityManager.getTransaction().commit();
        return resultList;
    }

    @Override
    public void addToBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException, InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var optUser = getUser(username, entityManager);
        if(optUser.isEmpty()) {
            entityManager.getTransaction().commit();
            throw new InvalidIdException("Invalid username");
        }
        var user = optUser.get();
        var buyList = entityManager.find(ShoppingList.class, user.getBuyListId());
        if(buyList == null) {
            buyList = new ShoppingList(user);
            entityManager.persist(buyList);
        }

        Commodity commodity = entityManager.find(Commodity.class, commodityId);

        buyList.getShoppingItemSet().add(new ShoppingItem(commodity, count));

        entityManager.getTransaction().commit();
    }

    @Override
    public void removeFromBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var optUser = getUser(username, entityManager);
        if(optUser.isEmpty()) {
            entityManager.getTransaction().commit();
            throw new InvalidIdException("Invalid username");
        }
        var user = optUser.get();
        var buyList = entityManager.find(ShoppingList.class, user.getBuyListId());
        if(buyList == null) {
            buyList = new ShoppingList(user);
            entityManager.persist(buyList);
        }

        Commodity commodity = entityManager.find(Commodity.class, commodityId);

        buyList.getShoppingItemSet().add(new ShoppingItem(commodity, count));

        entityManager.getTransaction().commit();
    }

    @Override
    public float addRating(@NotNull String username, long commodityId, float rate) {
        return 0.0F;
    }

    @Override
    public void addVote(@NotNull String voter, long commentId, int vote) throws InvalidIdException, InvalidValueException {

    }

    @Override
    public long getLikes(long commentId) {
        return 0;
    }

    @Override
    public long getDislikes(long commentId) {
        return 0;
    }

    @Override
    public List<Comment> getCommentsForCommodity(long commodityId) {
        return null;
    }

    @Override
    public void addCredit(String username, long credit) throws InvalidIdException, InvalidValueException {

    }

    @Override
    public void purchase(@NotNull String username, float discount) throws NotEnoughAmountException, InvalidIdException {

    }

    @Override
    public Optional<Float> getRating(@NotNull String username, long commodityId) {
        return Optional.empty();
    }

    @Override
    public long calculateTotalBuyListPrice(String username) throws InvalidIdException {
        return 0;
    }

    @Override
    public List<ShoppingItem> getShoppingList(String username) throws InvalidIdException {
        return null;
    }

    @Override
    public long getInShoppingListCount(String username, long commodityId) {
        return 0;
    }

    @Override
    public List<ShoppingItem> getPurchasedList(String username) throws InvalidIdException {
        return null;
    }

    @Override
    public Optional<Discount> getDiscount(String discountCode) {
        return Optional.empty();
    }

    @Override
    public boolean hasUserUsedDiscount(String discountCode, String username) throws InvalidIdException {
        return false;
    }

    @Override
    public List<Commodity> getRecommendedCommodities(String username, long commodityId) {
        return null;
    }

    @Override
    public Optional<Comment> getComment(long commentId) {
        return Optional.empty();
    }

    @Override
    public boolean authenticate(String username, String password) {
        return false;
    }

    @Override
    public long getCommodityRateCount(long commodityId) {
        return 0;
    }

    @Override
    public int getUserVoteForComment(String username, long commentId) {
        return 0;
    }
}