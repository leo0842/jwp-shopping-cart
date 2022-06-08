package woowacourse.shoppingcart.domain;

public class OrderDetail {

    private final Long productId;
    private final int quantity;
    private final int price;
    private final String name;
    private final String imageUrl;

    public OrderDetail(final Long productId, final int price, final String name,
                       final String imageUrl, final int quantity) {
        this.productId = productId;
        this.price = price;
        this.name = name;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public int calculateCost() {
        return price * quantity;
    }
}
