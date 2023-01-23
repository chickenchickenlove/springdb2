package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * 1. 마이바티스 매핑 XML을 호출해주는 맵퍼 인터페이스
 * 2. 사용하기 위해서 @Mapper 어노테이션 붙여야 MyBatis에서 인식함.
 * 3. 이 인터페이스의 메서드를 호출하면 xml의 해당 SQL을 실행하고 결과를 돌려줌.
 **/

 @Mapper
public interface ItemMapper {

    void save(Item item);

    void update(@Param("id") long id, @Param("updateParam") ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearch);


}
