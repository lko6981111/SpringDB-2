package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
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
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * NamedParameterJdbcTempalte
 * SqlParameterSource
 * - BeanPropertySqlParameterSource
 * - MapSqlParameterSource
 * Map
 *
 * BeanPropertyRowMapper
 *
 */
@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

    //private final JdbcTemplate template;//final 변수는 항상 생성자로 넣어줘야하나?
    private final NamedParameterJdbcTemplate template;

    /**
     * NamedParameterJdbcTemplate는 Spring Framework에서 제공하는 클래스로,
     * SQL 쿼리에서 파라미터를 명시적으로 이름을 붙여 사용할 수 있게 도와줍니다.
     * 일반적인 JdbcTemplate에서는 파라미터를 '?' 기호를 사용해 위치 기반으로 전달하지만,
     * NamedParameterJdbcTemplate은 파라미터에 이름을 부여하여 가독성을 높이고 실수를 줄일 수 있습니다.
     */

    public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
        // NamedParameterJdbcTemplate도 내부에 dataSource가 필요
        //생성자를 통해 의존관계 주입이 이루어짐

        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public Item save(Item item) {
        String sql = "insert into item(item_name,price,quantity) " +
                "values(:itemname,:price,:quantity)";
        //? -> :파라미터이름으로 변경됨
        //파라미터를 전달하려면 Map 처럼 key,value 데이터 구조를 만들어서 전달해야한다.

        SqlParameterSource param = new BeanPropertySqlParameterSource(item);
        //자바빈 프로퍼티 규약을 통해서 자동으로 파라미터 객체를 생성

        KeyHolder keyHolder = new GeneratedKeyHolder(); //db에서 생성한 key를 가져오기위해 jdbc에서 사용!
        template.update(sql,param,keyHolder);

        long key = keyHolder.getKey().longValue();
        item.setId(key);
        return item;

    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        String sql = "update item " +
                "set item_name=:itemName, price=:price, quantity=:quantity " +
                "where id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("itemName", updateParam.getItemName())
                .addValue("price", updateParam.getPrice())
                .addValue("quantity", updateParam.getQuantity())
                .addValue("id", itemId);
        //update()에서는 SQL에 :id를 바인딩 해야 하는데, update()에서 사용하는
        //ItemUpdateDto에는 itemId가 없다. 따라서 BeanPropertySqlParametrSource를 사용할 수 없고,
        //대신에 MapSqlParameterSource를 사용했다.


        template.update(sql,param);
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name,price,quantity from item where id =:id";
        try {
            Map<String,Long> param = Map.of("id",id);

            Item item = template.queryForObject(sql,param, itemRowMapper());
            return Optional.of(item);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();


        SqlParameterSource param = new BeanPropertySqlParameterSource(cond);

        String sql = "select id, item_name,price,quantity from item";
        //동적 쿼리
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            sql += " where";
        }
        boolean andFlag = false;

        if (StringUtils.hasText(itemName)) {
            sql += " item_name like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                sql += " and";
            }
            sql += " price <= :maxPrice";

        }
        log.info("sql={}", sql);
        return template.query(sql,param, itemRowMapper() );
    }

    private RowMapper<Item> itemRowMapper() {
        return BeanPropertyRowMapper.newInstance(Item.class); //camel 변환 지원
        //BeanPropertyRowMapper는 ResultSet의 결과를 받아서 자바빈 규약에 맞추어 데이터를 변환
    }
}
