package bot;

import java.io.FileWriter;
import java.io.IOException;

public class SaveOrders {

    public void save(int id, String name, String tgName, String phone, String address, StringBuilder order, int sum) {
        try (FileWriter fw = new FileWriter("result", true)) {
            String text = "Order №" + id +"\nName: " + name + "\nTelegram: " +
                   tgName + "\nPhone: " + phone + "\nAddress: " +address + "\n\n" + order + "\nTotal: " + sum;
            fw.write(text);
            fw.append('\n');
            fw.append('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
