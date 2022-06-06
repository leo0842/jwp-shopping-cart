package woowacourse.shoppingcart.application;

import java.util.List;
import woowacourse.shoppingcart.dto.ProductResponse;

public class CartResponse {

    private final List<ProductResponse> cart;

    public CartResponse(List<ProductResponse> cart) {
        this.cart = cart;
    }

    public List<ProductResponse> getCart() {
        return cart;
    }
}
