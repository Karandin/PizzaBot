package bot.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class Pizza {
private String name;
private String description;
private String bigDescription;
private String composition;
private String photo;
private int price;
private int priceBig;
private String id;
private boolean big;
}
