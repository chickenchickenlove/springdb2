package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate 구현
 * JdbcTempatel은 동적쿼리를 작성하는 것이 어렵다.
 */

@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

    private final JdbcTemplate template;

    // JdbcTemplate은 Connection이 필요하기 때문에 DataSource 주입이 필요함.
    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        // ID를 넣지 않았다.
        // 따라서 ID에 대해서 자동 생성하는 값을 만들고, 그 값을 꺼내와야 한다.
        // 메모리에서는 ++sequence를 이용해서 처리했었다.

        String sql = "insert into item(item_name, price, quantity) values (?,?,?)";

        // DB에서 생성해준 ID값을 가져오는 방법
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // Connection 넘기고 KeyHolder 넘기는 형식으로 짜주어야 한다.
        template.update(connection -> {
            // 자동 증가 키 // 아래와 같은 방식으로 사용해야함.
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);

        // DB에 들어간 Key 값을 KeyHolder가 가지고 있고, 이 값을 KeyHolder가 Return 해준다.
        // 왜냐하면 현재 DB의 테이블 전략이 Identity이기 때문이다.
        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item set item_name=?, price=?, quantity=? where id=?";


        /*
        template.update(sql, ps -> {
            ps.setString(1,updateParam.getItemName());
            ps.setInt(2, updateParam.getPrice());
            ps.setInt(3, updateParam.getQuantity());
        });
         */

        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name, price, quantity from item where id = ?";
        // quaryForObject : 쿼리한 후 객체를 하나 뽑는다. + RowMapper를 빼와야한다.
        // 값이 없는 경우 항상 EmptyResultDataAcessException가 터진다. 따라서 Try ~ Catch로 감싸야 한다.
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // 이 문제가 발생했다는 것은 데이터가 없다는 것이기 때문에 Empty를 반환해준다.
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name, price, quantity from item";

        // 동적 쿼리ㅏ
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',?,'%')";
            param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= ?";
            param.add(maxPrice);
        }

        log.info("sql = {}", sql);
        // Query는 List 가져올 때 사용. QueryForObject는 전체를 가져올 때
        return template.query(sql, itemRowMapper());
    }



    // Item을 반환하는 RowMapper 클래스를 보내주면 된다.
    // RowMapper는 ResultSet의 Cursor Loop를 직접 돌려준다.
    private RowMapper<Item> itemRowMapper() {
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
    }

}
