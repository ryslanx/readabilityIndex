package readability;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File file = new File(args[0]);
        TextAnalyzer analyzer = new TextAnalyzer(file);
        System.out.println("The text is:\n" + analyzer.getText());
        System.out.println();
        System.out.println("Words: " + analyzer.getWords());
        System.out.println("Sentences: " + analyzer.getSentences());
        System.out.println("Characters: " + analyzer.getCharacters());
        System.out.println("Syllables: " + analyzer.getSyllables());
        System.out.println("Polysyllables: " + analyzer.getPolysyllables());
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        String input = scanner.nextLine();
        switch (input) {
            case "ARI":
                System.out.printf("Automated Readability Index: %.2f (about %s year olds)\n",
                        analyzer.getReadIndexScore(), analyzer.checkAge(analyzer.getReadIndexScore()));
                break;
            case "FK":
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds)\n",
                        analyzer.getFleschKincaidScore(), analyzer.checkAge(analyzer.getFleschKincaidScore()));
                break;
            case "SMOG":
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds)\n",
                        analyzer.getSmogIndex(), analyzer.checkAge(analyzer.getSmogIndex()));
                break;
            case "CL":
                System.out.printf("Coleman–Liau index: %.2f (about %s year olds)\n",
                        analyzer.getSmogIndex(), analyzer.checkAge(analyzer.getSmogIndex()));
                break;
            case "all":
                double average = (Integer.parseInt(analyzer.checkAge(analyzer.getReadIndexScore())) +
                        Integer.parseInt(analyzer.checkAge(analyzer.getFleschKincaidScore())) +
                        Integer.parseInt(analyzer.checkAge(analyzer.getSmogIndex()))
                        + Integer.parseInt(analyzer.checkAge(analyzer.getSmogIndex()))) / 4.0;
                System.out.printf("Automated Readability Index: %.2f (about %s year olds)\n",
                        analyzer.getReadIndexScore(), analyzer.checkAge(analyzer.getReadIndexScore()));
                System.out.printf("Flesch–Kincaid readability tests: %.2f (about %s year olds)\n",
                        analyzer.getFleschKincaidScore(), analyzer.checkAge(analyzer.getFleschKincaidScore()));
                System.out.printf("Simple Measure of Gobbledygook: %.2f (about %s year olds)\n",
                        analyzer.getSmogIndex(), analyzer.checkAge(analyzer.getSmogIndex()));
                System.out.printf("Coleman–Liau index: %.2f (about %s year olds)\n",
                        analyzer.getColemanLiauIndex(), analyzer.checkAge(analyzer.getColemanLiauIndex()));
                System.out.println("This text should be understood in average by " + average + " year olds.");
        }


    }
}

class TextAnalyzer {
    String text;
    int sentences;
    int words;
    int characters;
    int syllables;
    int polysyllables;
    double readIndexScore;
    double fleschKincaidScore;
    double colemanLiauIndex;
    double smogIndex;
    File file;

    public TextAnalyzer(File file) {
        this.file = file;
        readFile();
        countCharacters();
        countWordsAndSentences();
        countReadabilityIndex();
        countSyllables();
        countFleschKincaid();
        countColemanLiauIndex();
        countSmogIndex();
    }

    public int getSyllables() {
        return syllables;
    }

    public int getPolysyllables() {
        return polysyllables;
    }

    public int getSentences() {
        return sentences;
    }

    public int getWords() {
        return words;
    }

    public String getText() {
        return text;
    }

    public int getCharacters() {
        return characters;
    }

    public double getReadIndexScore() {
        return readIndexScore;
    }

    public double getFleschKincaidScore() {
        return fleschKincaidScore;
    }

    public double getColemanLiauIndex() {
        return colemanLiauIndex;
    }

    public double getSmogIndex() {
        return smogIndex;
    }

    private void readFile() {
        try {
            text = new String(Files.readAllBytes(Paths.get(String.valueOf(file))));
        } catch (IOException e) {
            System.out.println("The file wasn't found");
        }
    }

    private void countCharacters() {
        String count = text.replace(" ", "");
        characters = count.length();
    }


    private void countSyllables() {
        String[] words = text.split(" |\n|\t");
        int countSyllables = 0;
        for (String word : words) {
            char[] chars = word.toCharArray();
            countSyllables = 0;
            for (int i = 0; i < chars.length - 1; i++) {
                if (Character.toString(chars[i]).toLowerCase().matches("[aeiouy]") &&
                        !Character.toString(chars[i+1]).toLowerCase().matches("[aeiouy]")) {
                    countSyllables++;
                }
            }
            if (countSyllables == 0) {
                countSyllables = 1;
            } else if (countSyllables > 2) {
                this.polysyllables += 1;
            }
            this.syllables += countSyllables;
        }
    }

    private void countWordsAndSentences() {
        String[] sentences = text.split("\\.|!|\\?");
        this.sentences = sentences.length;
        this.words = text.split(" |\n|\t").length;
    }

    private void countReadabilityIndex() {
        readIndexScore = 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
    }

    private void countFleschKincaid() {
        fleschKincaidScore = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }

    private void countSmogIndex() {
        smogIndex = 1.043 * Math.sqrt(polysyllables * (30.0 / sentences))   + 3.1291;
    }

    private void countColemanLiauIndex() {
        int letters = 0;
        String[] text = this.text.split("");
        for (String letter : text) {
            if (letter.matches("[a-zA-z]")) {
                letters++;
            }
        }
        colemanLiauIndex = 0.0588 * ((double) letters / words) * 100 - 0.296 * ((double) sentences / words) * 100 - 15.8;
    }

    public String checkAge(double score) {
        switch ((int) Math.ceil(score)) {
            case 1:
                return "6";
            case 2:
                return "7";
            case 3:
                return "9";
            case 4:
                return "10";
            case 5:
                return "11";
            case 6:
                return "12";
            case 7:
                return "13";
            case 8:
                return "14";
            case 9:
                return "15";
            case 10:
                return "16";
            case 11:
                return "17";
            case 12:
                return "18";
            case 13:
                return "24";
            case 14:
                return "24";
        }
        return "-1";
    }


}
