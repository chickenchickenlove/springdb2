package hello.itemservice.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity // JPA가 사용하는 객체라는 뜻. 이게 있어야 JPA가 인식함.
//@Table(name = "item") // 테이블명 = 객체명이면 생략해도 가능함.
public class Item {

    // DB에서 ID 값을 넣어줌.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name", length = 10)
    private String itemName;

    @Column(name = "price")
    private Integer price;

    // 컬럼명 == 필드명이면 @Column 생략 가능함.
    private Integer quantity;

    // JPA는 public으로 default 생성자가 필요함. (프록시를 위해서)
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
