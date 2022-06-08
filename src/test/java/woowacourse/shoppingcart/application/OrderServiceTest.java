package woowacourse.shoppingcart.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import woowacourse.shoppingcart.dao.CartItemDao;
import woowacourse.shoppingcart.dao.OrderDao;
import woowacourse.shoppingcart.dao.OrdersDetailDao;
import woowacourse.shoppingcart.domain.OrderDetail;
import woowacourse.shoppingcart.dto.OrderDetailResponse;
import woowacourse.shoppingcart.dto.OrderRequest;
import woowacourse.shoppingcart.dto.OrderResponse;
import woowacourse.shoppingcart.dto.OrdersRequest;
import woowacourse.shoppingcart.dto.OrdersResponse;

class OrderServiceTest {

    private final OrderService orderService;

    @Mock
    private OrderDao orderDao;
    @Mock
    private OrdersDetailDao ordersDetailDao;
    @Mock
    private CartItemDao cartItemDao;

    OrderServiceTest() {
        openMocks(this);
        this.orderService = new OrderService(orderDao, ordersDetailDao, cartItemDao);
    }

    @Test
    void addOrder() {
        //given
        Long productId1 = 1L;
        Long productId2 = 2L;
        Long customerId = 11L;
        Long ordersId = 101L;
        List<OrderRequest> orders = new ArrayList<>();
        orders.add(new OrderRequest(productId1, 3));
        orders.add(new OrderRequest(productId2, 5));

        //when
        given(orderDao.addOrders(customerId)).willReturn(ordersId);
        given(ordersDetailDao.addAllOrdersDetails(ordersId, List.of(productId1, productId2), List.of(3, 5))).willReturn(
                2);
        Long createdOrdersId = orderService.addOrder(customerId, new OrdersRequest(orders));

        //then
        assertThat(createdOrdersId).isEqualTo(101L);
    }

    @Test
    void findOrderById() {
        //given
        Long productId1 = 1L;
        Long customerId = 11L;
        Long ordersId = 101L;
        OrderDetail bananaOrderDetail = new OrderDetail(productId1, 1000, "banana", "banana.com", 2);
        given(orderDao.isValidOrderId(customerId, ordersId)).willReturn(true);

        given(ordersDetailDao.findOrdersDetailsByOrderId(ordersId)).willReturn(List.of(bananaOrderDetail));
        //when
        OrderResponse orderResponse = orderService.findOrderById(customerId, ordersId);
        //then
        assertAll(
                () -> assertThat(orderResponse.getOrderId()).isEqualTo(101),
                () -> assertThat(orderResponse.getTotalCost()).isEqualTo(2000),
                () -> {
                    OrderDetailResponse orderDetailResponse = orderResponse.getOrder().get(0);
                    assertAll(
                            () -> assertThat(orderDetailResponse.getCost()).isEqualTo(2000),
                            () -> assertThat(orderDetailResponse.getId()).isEqualTo(1),
                            () -> assertThat(orderDetailResponse.getName()).isEqualTo("banana"),
                            () -> assertThat(orderDetailResponse.getQuantity()).isEqualTo(2),
                            () -> assertThat(orderDetailResponse.getImageUrl()).isEqualTo("banana.com")
                    );
                }
        );
    }

    @Test
    void findOrdersByCustomerId() {
        //given
        Long productId1 = 1L;
        Long customerId = 11L;
        Long ordersId = 101L;
        OrderDetail bananaOrderDetail = new OrderDetail(productId1, 1000, "banana", "banana.com", 2);
        given(orderDao.findOrderIdsByCustomerId(customerId)).willReturn(List.of(ordersId));
        given(ordersDetailDao.findOrdersDetailsByOrderId(ordersId)).willReturn(List.of(bananaOrderDetail));
        //when
        OrdersResponse ordersResponse = orderService.findOrdersByCustomerId(customerId);
        //then
        assertAll(
                () -> assertThat(ordersResponse.getOrders().size()).isEqualTo(1),
                () -> assertThat(ordersResponse.getOrders().get(0).getTotalCost()).isEqualTo(2000)
        );
    }
}
