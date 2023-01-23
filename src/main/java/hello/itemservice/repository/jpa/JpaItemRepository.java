package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;


/**
 * 1. JPA를 그냥 사용하면 동적 쿼리 작성에 약하다.
 * 2. JPQL은 Table명을 alias를 해서 사용한다. select i from Item i... 이런 식으로
 * 3. :<parameter> 이런 식으로 NameParameter를 이용한다.
 *
 */
@Slf4j
@Repository
@Transactional // JPA는 트랜잭션을 이용해서 동작한다.
@RequiredArgsConstructor
public class JpaItemRepository implements ItemRepository {


    private final EntityManager em;

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    // 더티 체크를 이용한 업데이트.
    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {

        Item item = em.find(Item.class, itemId);

        item.setItemName(updateParam.getItemName());
        item.setPrice(updateParam.getPrice());
        item.setQuantity(updateParam.getQuantity());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(em.find(Item.class, id));
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();


        String jpql = "select i from Item i";
        boolean firstFlag = false;

        if (StringUtils.hasText(itemName) ) {
            jpql = jpql + " where i.itemName like concat('%', :itemName, '%')";
            firstFlag = true;
        }

        if (maxPrice != null) {

            if (firstFlag) {
                jpql = jpql + " and i.price <= :maxPrice";
            }else{
                jpql = jpql + " where i.price <= :maxPrice";
            }
        }


        log.info("jpql={}", jpql);

        TypedQuery<Item> query = em.createQuery(jpql, Item.class);

        if (StringUtils.hasText(itemName)) {
            query.setParameter("itemName", itemName);
        }

        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }

        return query.getResultList();
    }
}
