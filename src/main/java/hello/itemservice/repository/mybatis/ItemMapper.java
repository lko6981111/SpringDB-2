package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper// 이 인터페이스는 @Mapper 애노테이션을 붙여주어야 MyBatis에서 인식
public interface ItemMapper {
    //마이바티스 매핑 XML을 호출해주는 매퍼 인터페이스
    //이 인터페이스이 메서드를 호출하면 xml의 해당 SQL을 실해하고 결과를 돌려준다.
    void save(Item item);

    void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

    Optional<Item> findById(Long id);

    List<Item> findAll(ItemSearchCond itemSearch);

}
