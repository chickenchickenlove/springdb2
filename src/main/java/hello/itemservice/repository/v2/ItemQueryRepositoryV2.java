package hello.itemservice.repository.v2;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hello.itemservice.domain.Item;
import hello.itemservice.domain.QItem;
import hello.itemservice.repository.ItemSearchCond;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;

import java.util.List;

import static hello.itemservice.domain.QItem.item;

/**
 * 복잡한 쿼리는 이 곳에서 담당한다.
 * 복잡한 쿼리는 이 곳에서만 유지보수하면 된다. 복잡한 쿼리의 분리임.
 */
@Repository
public class ItemQueryRepositoryV2 {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public ItemQueryRepositoryV2(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public List<Item> findAll(ItemSearchCond cond) {
        return query
                .select(item)
                .from(item)
                .where(itemNameLike(cond.getItemName()), maxPrice(cond.getMaxPrice())) // null이면 무시함. "," 이걸로 하는 경우 and로 연결됨.
                .fetch();
    }

    private BooleanExpression itemNameLike(String itemName) {
        return StringUtils.hasText(itemName) ? item.itemName.like("%" + itemName + "%") : null;
    }

    private BooleanExpression maxPrice(Integer maxPrice) {
        return maxPrice != null ? item.price.loe(maxPrice) : null;
    }



}
