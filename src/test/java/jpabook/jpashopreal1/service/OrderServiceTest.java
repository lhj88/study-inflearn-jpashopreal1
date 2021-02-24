package jpabook.jpashopreal1.service;

import jpabook.jpashopreal1.domain.Address;
import jpabook.jpashopreal1.domain.Member;
import jpabook.jpashopreal1.domain.Order;
import jpabook.jpashopreal1.domain.OrderStatus;
import jpabook.jpashopreal1.domain.item.Book;
import jpabook.jpashopreal1.exception.NotEnoughStockException;
import jpabook.jpashopreal1.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook("JPA", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        System.out.println(OrderStatus.ORDER);
        System.out.println(getOrder.getStatus());
        System.out.println(getOrder.getStatus() == OrderStatus.ORDER);


        assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        assertEquals(1, getOrder.getOrderItems().size(),"주문한 상품 종류 수가 정확해야 한다.");
        assertEquals(10000*orderCount, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
        assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
    }



    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderid = orderService.order(member.getId(), book.getId(), orderCount);
        orderService.cancelOrder(orderid);

        //then
        Order getOrder = orderRepository.findOne(orderid);
        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL이다.");
        assertEquals(10, book.getStockQuantity(), "주문취소된 상품은 그만큼 재고가 증가해야 한다.");

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook("JPA", 10000, 10);

        //when
        int orderCount = 122;

        //then
        assertThrows(NotEnoughStockException.class, ()->{
            orderService.order(member.getId(), book.getId(), orderCount);
        });
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    private Book createBook(String jpa, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(jpa);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }
}