package hello.itemservice.repository.jpa;

import hello.itemservice.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataJpaItemRepository extends JpaRepository<Item,Long> {//엔티티, PK 자료형
//JpaRepository 인터페이스를 통해서 기본적인 CRUD 기능 제공, 공통화 가능한 기능이 거의 모두 포함
//JpaRepository 인터페이스를 인터페이스 상속 받고, 제네릭에 관리할 <엔티티, 엔티티ID>를 주면 된다.
//JpaRepository 인터페이스만 상속받으면 스프링 데이터 JPA가 프록시 기술을 사용해서 구현 클랫를 만들어준다.
//그리고 만든 구현 클래스의 인스턴스를 만들어서 스프링 빈으로 등록한다.


    List<Item> findByItemNameLike(String itemName);
    //쿼리메서드 : 스프링데이터 JPA는 인터페이스에 메서드만 적어두면, 메서드 이름을 분석해서 쿼리를 자동으로 만들고 실행해주는 기능 제공
    //스프링 데이터 JPA는 메서드 이름을 분석해서 필요한 JPQL을 만들고 실행해준다. 물론 JPQL은 JPA가 SQL로 번역해서 실행
    List<Item> findByPriceLessThanEqual(Integer price);

    //쿼리 메서드(아래 메서드와 같은 기능 수행)
    List<Item> findByItemNameLikeAndPriceLessThanEqual(String itemName, Integer price);

    // JPQL 쿼리 직접 실행
    //쿼리 메서드 기능 대신에 직접 JPQL을 사용하고 싶을 때는 @Query와 함께 JPQL을 작성하면 된다.
    //이때는 메서드 이름으로 실행하는 규칙은 무시된다.
    //참고로 스프링 데이터 JPA는 JPQL 뿐만 아니라 JPA의 네이티브 쿼리 기능도 지원하는데, JPQL 대신에 SQL
    //을 직접 작성할 수 있다.
    @Query("select i from Item i where i.itemName like :itemName and i.price<=:price")
    List<Item> finddItems(@Param("itemName") String itemName,@Param("price")Integer price);
}
