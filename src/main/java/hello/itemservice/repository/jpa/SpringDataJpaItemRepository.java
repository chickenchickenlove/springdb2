package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item, Long> {


    List<Item> findByItemNameLike(String itemName);

    List<Item> findByPriceLessThanEqual(Integer price);


    // 쿼리 메서드 --> 순서대로 파라메터가 들어가야 함. 아래 메서드가 동일한 기능을 수행하지만, 파라메터가 많아질수록.. 빡세다.
    List<Item> findByItemNameLikeAndPriceLessThanEqual(@Param("itemName") String itemName, @Param("price") Integer price);

    // JPQL을 직접 작성. 반드시 Param 넣어야 함.
    @Query("SELECT i from Item i where i.itemName like :itemName and i.price <= :price")
    List<Item> findItems(@Param("itemName") String itemName, @Param("price") Integer price);


}
