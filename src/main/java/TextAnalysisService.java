import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TextAnalysisService {

    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        Thread textGeneratorThread = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                String text = generateText("abc", 100_000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread characterCounterA = new Thread(() -> analyzeCharacter('a', queueA));
        Thread characterCounterB = new Thread(() -> analyzeCharacter('b', queueB));
        Thread characterCounterC = new Thread(() -> analyzeCharacter('c', queueC));

        textGeneratorThread.start();
        characterCounterA.start();
        characterCounterB.start();
        characterCounterC.start();

        textGeneratorThread.join();
        characterCounterA.join();
        characterCounterB.join();
        characterCounterC.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void analyzeCharacter(char character, BlockingQueue<String> queue) {
        int maxCount = 0;
        for (int i = 0; i < 10_000; i++) {
            try {
                String text = queue.take();
                int count = (int) text.chars().filter(ch -> ch == character).count();
                if (count > maxCount) {
                    maxCount = count;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Максимальное количество символов '" + character + "': " + maxCount);
    }
}
