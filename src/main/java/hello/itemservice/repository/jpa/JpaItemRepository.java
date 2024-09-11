package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
// JPA는 PersistenceException, IllegalStateException, IllegalArgumentException있다
//결과적으로 리포지토리에 @Repository는 애노테이션만 있으면 스프링이 예외 변환을 처리하는 AOP를 만들어준다.
@Transactional // JPA의 모든 데이터 변경(등록,수정,삭제)은 트랜잭션 안에서 이루어져야 한다.
//조회는 트랜잭션이 없어도 가능
public class JpaItemRepository implements ItemRepository {

    private final EntityManager em;
    //JPA의 모든 동작은 엔티티 매니저를 통해서 이루어진다.
    //엔티티 매니저는 내부에 데이터소스를 가지고 있고, 데이터베이스에 접근할 수 있다.

    public JpaItemRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Item save(Item item) {
        em.persist(item);
        //JPA에서 객체를 테이블에 저장할 때는 엔티티 매니저가 제공하는 persist() 메서드를 사용하면 된다.
        return item;
    }

    @Override
    public void update(Long itemId, ItemUpdateDto updateParam) {
        Item findItem = em.find(Item.class, itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
        //JPA는 트랜잭션이 커밋되는 시점에, 변경된 엔티티 객체가 있는지 확인
        //특정 엔티티 객체가 변경된 경우에는 UPDATE SQL을 실행
        //트랜잭션이 커밋되는 시점에 업데이트 쿼리를 JPA가 보내준다
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item item = em.find(Item.class, id);
        return Optional.ofNullable(item);
        //find()를 사용하고 조회 타입과, PK값을 주면 된다.
    }

    @Override
    public List<Item> findAll(ItemSearchCond cond) {
        String jpql = "select i from Item i";
        //JPQL, 이것은 엔티티 객체를 대상으로 SQL을 실행
        //from 다음에 Item 엔티티 객체 이름이 들어간다
        //엔티티 객체와 속성의 대소문자는 구분해야 한다.
        //결과적으로 JPQL을 실행하면 그 안에 포함된 엔티티 객체의 매핑 정보를 활용해서 SQL을 만들게 된다.
        Integer maxPrice = cond.getMaxPrice();
        String itemName = cond.getItemName();
        if (StringUtils.hasText(itemName) || maxPrice != null) {
            jpql  += " where";
        }
        boolean andFlag = false;
        if (StringUtils.hasText(itemName)) {
            jpql  += " i.itemName like concat('%',:itemName,'%')";
            andFlag = true;
        }
        if (maxPrice != null) {
            if (andFlag) {
                jpql += " and";
            }
            jpql += " i.price <= :maxPrice";
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
