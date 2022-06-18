package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * NamedParameterJdbcTemplate
 * Jdbc 파라메터를 이름으로 바인딩 해준다.
 */

@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    // NamedParameterJdbcTemplate으로 변경.
    //private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {

        // NamedParameterJdbcTemplate을 쓰면서 다음과 같이 변경됨.
        //String sql = "insert into item(item_name, price, quantity) values (?,?,?)";
        String sql = "insert into item(item_name, price, quantity) " +
                "values (:itemName,:price,:quantity)";

        /*
        template.update(connection -> {
            // 자동 증가 키 // 아래와 같은 방식으로 사용해야함.
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;
        }, keyHolder);
         */

        // Item을 DB에 저장하기 위해서 파라메터로 받아왔다.
        // 이 파라메터를 ParameterSource에 넘겨주면, 자동적으로 바인딩 된다. (객체의 이름으로 파라미터를 만든다)
        BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(item);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {

        //String sql = "update item set item_name=?, price=?, quantity=? where id=?";
        String sql = "update item set item_name=:itemName, price=:price, quantity=:quantity where id=:id";

        //NamedParameterJdbcTemplate을 사용하면서 다음과 같이 해결함.
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);

        template.update(sql, param);


        /*
        template.update(sql, ps -> {
            ps.setString(1,updateParam.getItemName());
            ps.setInt(2, updateParam.getPrice());
            ps.setInt(3, updateParam.getQuantity());
        });

        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId);
         */
    }

    @Override
    public Optional<Item> findById(Long id) {
        //String sql = "select id, item_name, price, quantity from item where id = ?";
        String sql = "select id, item_name, price, quantity from item where id = :id";

        try {
            // NamedParamJdbcTemplate을 사용
            Map<String, Long> param = Map.of("id", id);
            Item item = template.queryForObject(sql, param,itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // 이 문제가 발생했다는 것은 데이터가 없다는 것이기 때문에 Empty를 반환해준다.
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);
        String sql = "select id, item_name, price, quantity from item";

        // 동적 쿼리ㅏ
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }

        boolean andFlag = false;
        //List<Object> param = new ArrayList<>();
        if (StringUtils.hasText(itemName)) {

            //NamedParameterJdbcTemplate 사용
            //sql += " item_name like concat('%',?,'%')";
            sql += " item_name like concat('%',:itemName,'%')";
            //param.add(itemName);
            andFlag = true;
        }

        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            //NamedParameterJdbcTemplate 사용
            //sql += " price <= ?";
            sql += " price <= :maxPrice";
            //param.add(maxPrice);
        }

        log.info("sql = {}", sql);
        return template.query(sql, param,itemRowMapper());
    }


    private RowMapper<Item> itemRowMapper() {
        // Spring이 제공하는 RowMapper를 이용해서 넣어주면 됨.
        return BeanPropertyRowMapper.newInstance(Item.class); // Camel 변환 지원함.

        /*
        return (rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        };
         */
    }

}
