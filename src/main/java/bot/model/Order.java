package bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Pizza pizza;
    private int orderPrice;
    private int amount;
    private int messageId;
    private int resultMessageId;
    private Long orderChatID;



    public int calculateOrderPrice(int pizzaPrice, int amount, int sizePrice, boolean bigSize) {
        if (bigSize) {
            return amount * (pizzaPrice + sizePrice);
        }
        return amount * pizzaPrice;
    }
}
