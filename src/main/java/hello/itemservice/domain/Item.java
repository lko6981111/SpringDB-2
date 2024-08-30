package hello.itemservice.domain;

import lombok.Data;

@Data
public class Item {
    //Item은 상품 자체를 나타내는 객체이다. 이름,가격,수량을 속성으로 가지고 있다.
    //엔티티 선언을 통해 DB에 저장되는 객체들을 구현
    private Long id;

    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
