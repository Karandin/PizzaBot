package bot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client {
    private int id;
    private String name;
    private String tgName;
    private String phone;
    private String address;
    private String geo;
}
