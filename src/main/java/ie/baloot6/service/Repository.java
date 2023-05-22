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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);
        entityManager.persist(new Comment(user, commodity, commentText, Timestamp.valueOf(LocalDateTime.now())));

        entityManager.getTransaction().commit();
    }

    @Override
    public void addUser(String username, String password, String email, Date birthDate, String address, long credit) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
       entityManager.getTransaction().begin();

        try {
            User user = new User(username, password, email, birthDate, address, credit);
            entityManager.persist(user);
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw new InvalidIdException("Duplicated username");
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public User getUserByUsername(@NotNull String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return getUser(username, entityManager);
    }



    @Override
    public void addProvider(@NotNull String name, long providerId) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Provider provider = new Provider(name, Date.valueOf(LocalDate.now()));
        provider.setProviderId(providerId);
        entityManager.persist(provider);

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

        Provider provider = entityManager.find(Provider.class, providerId);
        if (provider == null) {
            entityManager.getTransaction().commit();
            throw new InvalidIdException("Invalid provider id");
        }

        entityManager.persist(new Commodity(name, provider, price, inStock));

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

        var resultList = entityManager.createQuery("SELECT c from Commodity c").getResultList();

        return resultList;
    }

    @Override
    public List<Commodity> getProvidersCommoditiesList(long providerId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("SELECT c from Commodity c where c.provider.providerId=:providerId")
                .setParameter("providerId", providerId)
                .getResultList();

        return resultList;
    }

    @Override
    public Optional<Commodity> getCommodityById(long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var commodity = entityManager.find(Commodity.class, id);

        return Optional.ofNullable(commodity);
    }

    @Override
    public List<Commodity> getCommoditiesByCategory(@NotNull String categoryName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Optional<Category> category = getCategoryByName(categoryName, entityManager);
        if(category.isEmpty()) {
            return new ArrayList<>();
        }

        var commodities = category.get().getCommoditySet().stream().toList();
        entityManager.getTransaction().commit();
        return category.get().getCommoditySet().stream().toList();
    }

    @Override
    public List<Commodity> getCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name=:name")
                .setParameter("name", name)
                .getResultList();

        return resultList;
    }

    @Override
    public List<Commodity> searchCommoditiesByName(@NotNull String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select c from Commodity c where c.name like :name")
                .setParameter("name", name)
                .getResultList();

        return resultList;
    }

    @Override
    public void addToBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException, InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);

        var resultList = entityManager.createQuery("select si from ShoppingItem si " +
                "                                             where si.user.userId=:userId " +
                "                                                  and si.beenPurchased=false " +
                "                                                   and si.commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();

        ShoppingItem shoppingItem;
        if(resultList.isEmpty()) {
            shoppingItem = new ShoppingItem(user, commodity, count, false);
            entityManager.persist(shoppingItem);
        } else
        {
            shoppingItem = (ShoppingItem) resultList.get(0);
            shoppingItem.setCount(shoppingItem.getCount() + count);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public void removeFromBuyList(@NotNull String username, long commodityId, long count) throws NotEnoughAmountException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);

        var resultList = entityManager.createQuery("select si from ShoppingItem si " +
                                                            " where si.user.userId=:userId " +
                                                            " and si.beenPurchased=false " +
                                                            " and si.commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();

        ShoppingItem shoppingItem;
        if(resultList.isEmpty()) {
            throw new NotEnoughAmountException("No shopping Item to remove from");
        } else
        {
            shoppingItem = (ShoppingItem) resultList.get(0);
            if(shoppingItem.getCount() >= count) {
                shoppingItem.setCount(shoppingItem.getCount() - count);
            } else {
                shoppingItem.setCount(0);
            }
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public double addRating(@NotNull String username, long commodityId, double rate) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        Commodity commodity = getCommodity(commodityId, entityManager);
        Rating rating;

        var resultList = entityManager.createQuery("select r from Rating r where user.userId=:userId and" +
                                                           " commodity.commodityId=:commodityId")
                .setParameter("userId", user.getUserId())
                .setParameter("commodityId", commodityId)
                .getResultList();
        if(resultList.isEmpty()) {
            rating = new Rating(user, commodity, rate);
            entityManager.persist(rating);
        }
        else {
            rating = (Rating) resultList.get(0);
            rating.setRating(rate);
        }

        entityManager.getTransaction().commit();
        return getUserRating(username, commodityId).get();
    }

    @Override
    public void addVote(@NotNull String voter, long commentId, int voteValue) throws InvalidIdException, InvalidValueException {
        if(voteValue < -1 || 1 < voteValue) {
            throw new InvalidValueException("Invalid vote value");
        }
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        User user = getUser(voter, entityManager);
        Comment comment = getComment(commentId, entityManager);

        var resultList = entityManager.createQuery("select v from Vote v where v.user.userId=:userId and" +
                                                      " v.comment.commentId=:commentId")
                .setParameter("userId", user.getUserId())
                .setParameter("commentId", commentId)
                .getResultList();
        Vote vote;
        if(resultList.isEmpty()) {
            vote = new Vote(comment, user, voteValue);
            entityManager.persist(vote);
        }
        else {
            vote = (Vote) resultList.get(0);
            vote.setVote(voteValue);
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public long getLikes(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select sum(v.vote) from Vote v" +
                " where v.comment.commentId=:commentId and v.vote=1")
                .setParameter("commentId", commentId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public long getDislikes(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        var resultList = entityManager.createQuery("select sum(v.vote) from Vote v" +
                                                                                " where v.comment.commentId=:commentId and v.vote=-1")
                .setParameter("commentId", commentId)
                .getResultList();
        return resultList.isEmpty() || resultList.get(0) == null ? 0 : (Long) resultList.get(0);
    }

    @Override
    public List<Comment> getCommentsForCommodity(long commodityId) {
        return entityManagerFactory.createEntityManager().createQuery("select c from Comment c where c.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getResultList();
    }

    @Override
    public void addCredit(String username, long credit) throws InvalidIdException, InvalidValueException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        user.setCredit(user.getCredit() + credit);

        entityManager.getTransaction().commit();
    }

    @Override
    public void purchase(@NotNull String username, float discount) throws NotEnoughAmountException, InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        User user = getUser(username, entityManager);
        var shoppingItems = (List<ShoppingItem>)entityManager.createQuery("select si from ShoppingItem si " +
                                                                            "where user.userId=:userId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .getResultList();

        long price = 0;
        for(var shoppingItem : shoppingItems) {
            price += shoppingItem.getCommodity().getPrice() * shoppingItem.getCount();
            if(shoppingItem.getCommodity().getInStock() < shoppingItem.getCount()) {
                entityManager.getTransaction().rollback();
                throw new NotEnoughAmountException("Not enough in stock");
            }
        }
        price = (long)(price * discount);

        if(price > user.getCredit()) {
            entityManager.getTransaction().rollback();
            throw new NotEnoughAmountException("Not enough credit");
        }
        else {
            user.setCredit(user.getCredit() - price);
            for(var shoppingItem : shoppingItems) {
                shoppingItem.setBeenPurchased(true);
                shoppingItem.getCommodity().subtractStock(shoppingItem.getCount());
            }
        }
        entityManager.getTransaction().commit();
    }

    @Override
    public Optional<Double> getCommodityRating(long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        return Optional.ofNullable((Double) entityManager.createQuery("select avg(r.rating) from Rating r " +
                "where r.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getSingleResult());
    }

    @Override
    public Optional<Double> getUserRating(@NotNull String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return Optional.ofNullable((Double) entityManager.createQuery("select r.rating from Rating r " +
                                                                        "where r.commodity.commodityId=:commodityId and user.userId=:userId")
                .setParameter("commodityId", commodityId)
                .setParameter("userId", user.getUserId())
                .getSingleResult());
    }

    @Override
    public long calculateTotalBuyListPrice(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return (Long) entityManager.createQuery(
                "select sum (si.count * si.commodity.price) " +
                    "from ShoppingItem si " +
                        "where si.user.userId=:userId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .getSingleResult();
    }

    @Override
    public List<ShoppingItem> getShoppingList(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return entityManager.createQuery(
                "select si from ShoppingItem si where si.user.userId=:userId and si.beenPurchased=false")
                .setParameter("userId", user.getUserId())
                .getResultList();
    }

    @Override
    public long getInShoppingListCount(String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return (Long) entityManager.createQuery(
                "select distinct(si.commodity.commodityId) from ShoppingItem si where si.user.userId=:userId and si.beenPurchased=false ")
                .setParameter("userId", user.getUserId())
                .getSingleResult();
    }

    @Override
    public List<ShoppingItem> getPurchasedList(String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        return entityManager.createQuery(
                "select si from ShoppingItem si where si.user.userId=:userId and si.beenPurchased=true")
                .setParameter("userId", user.getUserId())
                .getResultList();
    }

    @Override
    public Optional<Discount> getDiscount(String discountCode) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return Optional.of(getDiscount(discountCode, entityManager));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean hasUserUsedDiscount(String discountCode, String username) throws InvalidIdException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);
        Discount discount = getDiscount(discountCode, entityManager);

        return !entityManager.createNativeQuery(
                "select * from discountUses " +
                        "where discountId = :discountId and userId = :userId")
                    .setParameter("discountId", discount.getDiscountId())
                    .setParameter("userId", user.getUserId())
                    .getResultList().isEmpty();


    }

    @Override
    public List<Commodity> getRecommendedCommodities(String username, long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        HashMap<Long, Double> scores = new HashMap<>();
        Commodity commodity = getCommodity(commodityId, entityManager);

        var commodities = (List<Commodity>)entityManager.createQuery("select c from Commodity c").getResultList();

        commodities.forEach( c -> {
            var commonCategories = new HashSet<>(commodity.getCategorySet());
            commonCategories.addAll(c.getCategorySet());
            scores.put(c.getCommodityId(), 11 * commonCategories.size() + getUserRating(username, c.getCommodityId()).get());
        });
        scores.remove(commodityId);
        return scores.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(
                cid -> getCommodity(cid.getKey(), entityManager)
        ).toList().subList(0, 5);

    }

    @Override
    public Optional<Comment> getComment(long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return Optional.of(getComment(commentId, entityManager));
    }

    @Override
    public boolean authenticate(String username, String password) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            User user = getUser(username, entityManager);
            if(user.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public long getCommodityRateCount(long commodityId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        return (Long) entityManager.createQuery(
                "select count (*) from Rating r " +
                    "where r.commodity.commodityId=:commodityId")
                .setParameter("commodityId", commodityId)
                .getSingleResult();
    }

    @Override
    public int getUserVoteForComment(String username, long commentId) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = getUser(username, entityManager);

        var resultList =  entityManager.createQuery(
                    "select v.vote from Vote v " +
                    "where v.user.userId=:userId")
                .setParameter("userId", user.getUserId())
                .getResultList();

        return resultList.isEmpty() ? 0 : (Integer) resultList.get(0);
    }

    @Override
    public void addCategory(String categoryName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(new Category(categoryName));

        entityManager.getTransaction().commit();

    }

    private Optional<Category> getCategoryByName(@NotNull String categoryName, EntityManager entityManager) {
        var resultList = entityManager.createQuery("select c from Category c where c.categoryName=:categoryName")
                .setParameter("categoryName", categoryName).getResultList();

        return resultList.size() == 1 ?
                Optional.ofNullable((Category) resultList.get(0)) :
                Optional.empty();
    }

    @NotNull
    private User getUser(@NotNull String username, EntityManager entityManager) throws InvalidIdException {
        List users = entityManager.createQuery("SELECT u FROM User u where u.username=:username")
                .setParameter("username", username).getResultList();
        if(users.isEmpty()) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid user id");
        }
        return (User) users.get(0);
    }

    @NotNull
    public Commodity getCommodity(long commodityId, EntityManager entityManager) throws InvalidIdException {
        Commodity commodity = entityManager.find(Commodity.class, commodityId);
        if(commodity == null) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid commodity Id");
        }
        return commodity;
    }

    @NotNull
    private Discount getDiscount(@NotNull String discountCode, EntityManager entityManager) throws InvalidIdException {


        var resultList = entityManager.createQuery("select d from Discount d where d.discountCode=:discountCode")
                .setParameter("discountCode", discountCode)
                .getResultList();
        if(resultList.isEmpty()) {
            throw new InvalidIdException("Invalid discount code");
        }

        return (Discount) resultList.get(0);

    }


    @NotNull
    private Comment getComment(long commentId, EntityManager entityManager) throws InvalidIdException {
        Comment comment = entityManager.find(Comment.class, commentId);
        if(comment == null) {
            if(entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new InvalidIdException("Invalid comment Id");
        }
        return comment;
    }

}