package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
//이 어노테이션을 클래스에 붙이면 롬복이 자동으로 다음 메서드들을 생성
//Getter,Setter,toString(),equals(),@RequiredArgsConstructor
@Entity // JPA가 사용하는 객체, 이 어노테이션이 있어야 JPA가 인식할 수 있다, 테이블이랑 같이 mapping 됨
public class Item {
    //Item은 상품 자체를 나타내는 객체이다. 이름,가격,수량을 속성으로 가지고 있다.
    //엔티티 선언을 통해 DB에 저장되는 객체들을 구현

    @Id// 테이블의 PK와 해당 필드를 매핑
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //PK생성 값을 데이터베이스에서 생성하는 IDENTITY 방식을 사용
    private Long id;

    @Column(name = "item_name", length = 10)
    //객체의 필드를 테이블의 컬럼과 매핑
    //lengh = 10을 사용하여 매핑정보로 DDL도 생성하는데, 그때 컬럼의 길이값으로 활용
    //@Column을 생략할 경우 필드의 이름을 테이블 컬럼 이름으로 사용
    //참고로 지금처럼 스프링부트와 통합해서 사용하면 피드 이름을 테이블 컬럼 명으로 변경할 때, 객체 필드의 카멜 케이스를 테이블 컬럼의 언더스코어로 자동으로 변환
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
        //JPA는 public 또는 protected의 기본 생성자가 필수, 기본 생성자를 꼭 넣어주자!
        //참고로 이것을 기반으로 프록시 기술을 사용하기때문이다.
    }

    public Item(String itemName, Integer price, Integer quantity) {
        //Item 객체에 대한 생성자
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
