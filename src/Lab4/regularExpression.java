package src.Lab4;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class regularExpression {

    private static final Random random = new Random();

    public static void main(String[] args) {
        String combination1 = generateFirstRegex();
        System.out.println("1. (a|b)(c|d)E^+G:");
        System.out.println(combination1);

        String combination2 = generateSecondRegex();
        System.out.println("2. P(Q|R|S)T(UV|W|X)^*Z^+:");
        System.out.println(combination2);

        String combination3 = generateThirdRegex();
        System.out.println("3. 1(0|1)^*2(3|4)^(5)36:");
        System.out.println(combination3);
    }

    private static String generateFirstRegex() {
        List<String> results = new ArrayList<>();

        for (String a : new String[]{"a", "b"}) {
            for (String b : new String[]{"c", "d"}) {
                for (int repetitions = 1; repetitions <= 5; repetitions++) {
                    results.add(a + b + "E".repeat(repetitions) + "G");
                }
            }
        }

        return selectRandom(results);
    }

    private static String generateSecondRegex() {
        List<String> results = new ArrayList<>();

        for (String a : new String[]{"Q", "R", "S"}) {
            for (String b : new String[]{"", "UV", "W", "X"}) {
                for (int repetitions = 1; repetitions <= 5; repetitions++) {
                    results.add("P" + a + "T" + b.repeat(repetitions) + "Z".repeat(repetitions));
                }
            }
        }

        return selectRandom(results);
    }

    private static String generateThirdRegex() {
        List<String> results = new ArrayList<>();

        for (String a : new String[]{"0", "1"}) {
            for (String b : new String[]{"33333", "44444"}) {
                for (int repetitions = 1; repetitions <= 5; repetitions++) {
                    results.add("1" + a.repeat(repetitions) + "2" + b + "36");
                }
            }
        }

        return selectRandom(results);
    }

    private static String selectRandom(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }
}