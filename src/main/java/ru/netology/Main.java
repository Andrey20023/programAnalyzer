package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(99);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(99);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(99);
    public static Thread textGenerator;

    public static void main(String[] args) throws InterruptedException {
        textGenerator = new Thread(() -> {
            for (int i = 0; i < 9999; i++) {
                String text = (generateText("abc", 99999));
                //System.out.println(text);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread a = getThread(queueA, 'a');
        Thread b = getThread(queueB, 'b');
        Thread c = getThread(queueC, 'c');
        a.start();
        b.start();
        c.start();
        a.join();
        b.join();
        c.join();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread getThread(BlockingQueue<String> queue, char ch) {
        return new Thread(() -> {
            int count = searchStringNumberCharacters(queue, ch);
            System.out.printf("Символ : %s встречается %s раз.  ", ch, count);
        });
    }

    public static int searchStringNumberCharacters(BlockingQueue<String> queue, char ch) {
        int max = 0;
        int count = 0;
        String str;
        try {
            while (textGenerator.isAlive()) {
                str = queue.take();
                for (char c : str.toCharArray()) {
                    if (c == ch) count++;
                }
                if (count > max) max = count;
                count = 0;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return max;
    }
}

