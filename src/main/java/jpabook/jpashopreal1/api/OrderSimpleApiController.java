package jpabook.jpashopreal1.api;

import jpabook.jpashopreal1.domain.Address;
import jpabook.jpashopreal1.domain.Order;
import jpabook.jpashopreal1.domain.OrderStatus;
import jpabook.jpashopreal1.repository.OrderRepository;
import jpabook.jpashopreal1.repository.OrderSearch;
import jpabook.jpashopreal1.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashopreal1.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * xToOne
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /*@GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        all.stream().map(o -> new OrderDto
        return all;
    }*/

    @GetMapping("/api/v2/simple-orders")
    public Result ordersV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> collect = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @GetMapping("/api/v3/simple-orders")
    public Result ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public Result ordersV4(){

        List<OrderSimpleQueryDto> orderDtos = orderSimpleQueryRepository.findOrderDtos();
        return new Result(orderDtos);
    }

    @Data
    @AllArgsConstructor
    static class Result<T>{
        private T Data;
    }

    @Data
    static class SimpleOrderDto{
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }
}
