import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main {
    static FlowLogParser parser = new FlowLogParser();

    private static final List<String> PROTOCOLS = Arrays.asList(
            "tcp", "udp", "icmp", "http", "https", "ftp", "smtp", "dns"
    );

    private static final List<String> TAGS = Arrays.asList(
            "sv_P1", "sv_P2", "sv_P3", "sv_P4", "sv_P5", "email", "sv_P6", "sv_P7"
    );

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    public static void main(String[] args) {
        testBasicFunctionality();
        generateLargeFile();
        generateLargeLookupNumbers();
        testLargeFilesFunctionality();
    }

    private static void testBasicFunctionality(){
        parser.loadProtocolNumbers("src/input/protocol-numbers.csv");
        parser.loadLookupTable("src/input/lookup-basic.csv");
        parser.processFlowLog("src/input/flow-logs-basic.txt");
        parser.extractResults("basicTagCounts", "basicPortProtocolCounts");
    }

    private static void testLargeFilesFunctionality(){
        parser.loadProtocolNumbers("src/input/protocol-numbers.csv");
        parser.loadLookupTable("src/input/lookup-large.csv");
        parser.processFlowLog("src/input/flow-logs-large.txt");
        parser.extractResults("largeTagCounts", "largePortProtocolCounts");
    }

    public static void generateLargeFile() {
        String filePath = "src/input/flow-logs-large.txt";
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < 88880; i++) {
                long startTime = 1620140761 + random.nextInt(10000);
                long endTime = startTime + random.nextInt(100);

                String action = random.nextBoolean() ? "ACCEPT" : "REJECT";

                writer.write("2 123456789012 eni-" + i + " 10.0.1." + (i % 255) + " 198.51.100." + (i % 255)
                        + " 443 " + (random.nextInt(MAX_PORT - MIN_PORT  + 1) + MIN_PORT ) + " "
                        + random.nextInt(256) + " 25 20000 " + startTime + " " + endTime + " "
                        + action + " OK\n");
            }
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while generating large flow log file: " + e.getMessage());
        }
    }

    public static void generateLargeLookupNumbers() {
        String filePath = "src/input/lookup-large.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            Set<String> generatedEntries = new HashSet<>();

            for (int i = 0; i < 99999; i++) {
                int dstPort = getRandomPort();
                String protocol = getRandomProtocol();
                String tag = getRandomTag();

                String entry = dstPort + "," + protocol + "," + tag;

                while (generatedEntries.contains(entry)) {
                    tag = getRandomTag();
                    entry = dstPort + "," + protocol + "," + tag;
                }

                generatedEntries.add(entry);
                writer.write(entry + "\n");
            }
        } catch (IOException e) {
            System.err.println("An unexpected error occurred while generating large lookup table file: " + e.getMessage());
        }
    }

    private static int getRandomPort() {
        Random random = new Random();
        return random.nextInt(MAX_PORT - MIN_PORT + 1) + MIN_PORT;
    }

    private static String getRandomProtocol() {
        Random random = new Random();
        return PROTOCOLS.get(random.nextInt(PROTOCOLS.size()));
    }

    private static String getRandomTag() {
        Random random = new Random();
        return TAGS.get(random.nextInt(TAGS.size()));
    }
}
