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
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JdbcTemplate
 */
@Slf4j
@Repository
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {
//ItemRepository인터페이스를 구현
    private final JdbcTemplate template;//final 변수는 항상 생성자로 넣어줘야하나?

    public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);//JdbcTemplate은 dataSource가 필요
        //dataSource를 의존 관계 주입 받고 생성자 내부에서 JdbcTemplate을 생성한다. 스프링에서 JdbcTemplate을 사용할 때
        //관례상 이 방법을 사용
    }

    @Override
    public Item save(Item item) {//데이터를 저장
        String sql = "insert into item(item_name,price,quantity) values(?,?,?)";//save sql문
        KeyHolder keyHolder = new GeneratedKeyHolder(); //db에서 생성한 key를 가져오기위해 jdbc에서 사용!
        template.update(connection -> {//template.update()의 반환 값은 int인데, 영향 받은 로우 수를 반환
            //KeyHolder와 connection.prepareStatement(sql,new String[]{"id"}를 사용해서
            //id를 지정해주면 INSERT 쿼리 실행 이후에 데이터베이스에서 생성된 id값을 조회할 수 있다.
            //자동 증가 키
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, item.getItemName());
            ps.setInt(2, item.getPrice());
            ps.setInt(3, item.getQuantity());
            return ps;

        }, keyHolder);

        long key = keyHolder.getKey().longValue();// 삽입 후 자동 생성된 ID 값 가져오기
        item.setId(key);//key값 설정
        return item;

    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {//데이터를 업데이트
        String sql = "update item set item_name=?, price=?, quantity=?, where id=?";
        //?에 바인딩할 파라미터를 순서대로 전달
        template.update(sql,
                updateParam.getItemName(),
                updateParam.getPrice(),
                updateParam.getQuantity(),
                itemId
        );
    }

    @Override
    public Optional<Item> findById(Long id) {
        String sql = "select id, item_name,price,quantity from item where id =?";
        try {
            Item item = template.queryForObject(sql, itemRowMapper(), id);
            //template.queryForObject() : 결과 로우가 하나일 때 사용
            //RowMapper는 데이터베이스의 반환 결과인 ResultSet을 객체로 변환
            return Optional.of(item);//null인지 검사,null이면 EmptyResultDataAccessException 발생
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        //데이터를 리스트로 조회
        String itemName = cond.getItemName();
        Integer maxPrice = cond.getMaxPrice();

        String sql = "select id, item_name,price,quantity from item";
        //동적 쿼리
        //결과적으로 4가지 상황에 따른 SQL을 동적으로 생성해야 한다.
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
        log.info("sql={}", sql);
        return template.query(sql, itemRowMapper(), param.toArray());
        //결과가 하나 이상일 때 사용
    }

    private RowMapper<Item> itemRowMapper() {
        //RowMapper는 Spring Framework에서 JdbcTemplate을 사용하여 SQL 쿼리 결과를 객체로 매핑할 때 사용하는 인터페이스입니다.
        // 즉, 데이터베이스의 각 행(Row)을 특정 객체로 변환하는 역할을 합니다.
        //T mapRow(ResultSet rs, int rowNum): 데이터베이스로부터 가져온 각 행(ResultSet)을 매핑하여 원하는 객체로 변환합니다.
        // 이 메서드에서 객체로 변환된 행은 JdbcTemplate이 반환합니다.
        return ((rs, rowNum) -> {
            Item item = new Item();
            item.setId(rs.getLong("id"));
            item.setItemName(rs.getString("item_name"));
            item.setPrice(rs.getInt("price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        });

        //데이터베이스의 조회 결과를 객체로 변환할 때 사용한다.
        //JdbcTemplate에서 자체적으로 루푸를 돌려주고, 개발자는 RowMapper를 구현해서 그 내부 코드만 채운다고 이해하면 된다.

        //1. SQL 쿼리를 실행하여 ResultSet을 얻습니다.
        //2. UserRowMapper가 ResultSet의 각 행을 순차적으로 읽으며 User 객체로 변환합니다.
        //3. 변환된 객체 리스트를 반환하여 사용할 수 있습니다.
    }
}
