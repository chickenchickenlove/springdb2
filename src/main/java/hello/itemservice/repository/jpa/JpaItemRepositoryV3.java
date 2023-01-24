package hello.itemservice.repository.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static hello.itemservice.domain.QItem.*;

@Repository
@Transactional
public class JpaItemRepositoryV3 implements ItemRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public JpaItemRepositoryV3(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em); // QueryDSL은 JPQL을 만들어주는 역할을 한다. JPA는 Entity Manager를 가지고 동작한다.
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {

        Item findItem = em.find(Item.class, itemId);

        findItem.setItemName(updateParam.getItemName());
        findItem.setQuantity(updateParam.getQuantity());
        findItem.setPrice(updateParam.getPrice());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(em.find(Item.class, id));
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {

        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        return queryFactory
                .select(item)
                .from(item)
                .where(itemNameLike(itemName), maxPrice(maxPrice)) // null이면 무시함. "," 이걸로 하는 경우 and로 연결됨.
                .fetch();
    }

    private BooleanExpression itemNameLike(String itemName) {
        return StringUtils.hasText(itemName) ? item.itemName.like("%" + itemName + "%") : null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        return maxPrice != null ? item.price.loe(maxPrice) : null;
    }

}
