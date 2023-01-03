package bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeliveryStatus {
    private boolean sendPhone;
    private boolean sendAdress;
    private boolean waitConfirm;
}
