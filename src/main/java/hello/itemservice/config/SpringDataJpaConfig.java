package hello.itemservice.config;

import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepository;
import hello.itemservice.repository.jpa.JpaItemRepositoryV2;
import hello.itemservice.repository.jpa.SpringDataJpaItemRepository;
import hello.itemservice.service.ItemService;
import hello.itemservice.service.ItemServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Configuration
public class SpringDataJpaConfig {

   private final SpringDataJpaItemRepository springDataJpaItemRepository;

   /*
   * IDE가 친절하게 알려주는 경고입니다.
   * 보통 spring data jpa 의 jpaRepository를 상속해서 만든 repository는 애플리케이션 실행 시에
   * 동적으로 해당 repository의 구현체를 만들어 스프링 빈으로 등록합니다.
   * 그런데 지금은 애플리케이션 실행 전이기 때문에 해당 타입의 빈이 없고 IDE는 해당 빈이 없는데? 괜찮은거 맞아? 하면서 경고성 문구를 띄워주는 것입니다.
   * 정상적으로 진행할 수 있으시다면 무시하셔도 됩니다!*/

    @Bean
    public ItemService itemService() {
        return new ItemServiceV1(itemRepository());
    }
    @Bean
    public ItemRepository itemRepository() {
        return new JpaItemRepositoryV2(springDataJpaItemRepository);
    }
}