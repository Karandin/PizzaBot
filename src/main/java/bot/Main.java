package bot;

public class Main {
    private static final String BOT_TOKEN = "5831431529:AAGrhSZ1ODO5xxWgCCilisd3cgDXi0vwLwY";

    public static void main(String[] args) {
        System.out.println("go");
        TelegramBotApplication application = TelegramBotApplication.builder()
                .botToken(BOT_TOKEN)
                .build();

        application.run();
    }
}