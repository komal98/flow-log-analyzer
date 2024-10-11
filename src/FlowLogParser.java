import java.io.*;
import java.util.*;

/**
 * The FlowLogParser class processes flow log data, mapping network protocols and ports
 * to associated tags. It can load protocol mappings from a file, read lookup tables for
 * tagging information, process flow logs to count occurrences of tags and port/protocol
 * combinations, and output the results to CSV files.
 */
public class FlowLogParser {
    private final Map<String, Integer> tagCounts = new HashMap<>();
    private final Map<String, Integer> portProtocolCounts = new HashMap<>();
    private final Map<String, Set<String>> lookupTable = new HashMap<>();
    private final Map<String, String> protocolNumbers = new HashMap<>();

    /**
     * Loads protocol numbers from a specified file.
     *
     * @param protocolNumbersFile the path to the file containing protocol numbers and their names
     */
    public void loadProtocolNumbers(String protocolNumbersFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(protocolNumbersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] protocolNumbersRow = line.split(",");
                if (protocolNumbersRow.length >= 2) {
                    protocolNumbers.put(protocolNumbersRow[0].trim(), protocolNumbersRow[1].trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading protocol numbers from " + protocolNumbersFile + ": " + e.getMessage());
        }
    }

    /**
     * Loads a lookup table from a specified file.
     * The lookup table associates destination ports and protocols with tags.
     *
     * @param lookupFile the path to the file containing lookup table entries
     */
    public void loadLookupTable(String lookupFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(lookupFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] lookupTableRow = line.split(",");
                if (lookupTableRow.length == 3) {
                    String dstPortAndProtocolKey = lookupTableRow[0].trim() + "," + lookupTableRow[1].trim().toLowerCase();
                    String tag = lookupTableRow[2].trim().toLowerCase();
                    lookupTable.computeIfAbsent(dstPortAndProtocolKey, k -> new HashSet<>()).add(tag);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading lookup table from " + lookupFile + ": " + e.getMessage());
        }
    }

    /**
     * Processes a flow log file to count occurrences of tags and port/protocol combinations.
     *
     * @param flowLogFile the path to the flow log file to be processed
     */
    public void processFlowLog(String flowLogFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] processFlowLogRow = line.split(" ");
                if (processFlowLogRow.length >= 6) {
                    String dstPort = processFlowLogRow[6];
                    String numericProtocol = processFlowLogRow[7];
                    String protocolName = protocolNumbers.get(numericProtocol);
                    String dstPortAndProtocolKey = dstPort.trim() + "," + protocolName;

                    Set<String> tags = lookupTable.getOrDefault(dstPortAndProtocolKey, new HashSet<>());

                    if (tags.isEmpty()) {
                        tagCounts.put("Untagged", tagCounts.getOrDefault("Untagged", 0) + 1);
                    } else {
                        for (String tag : tags) {
                            tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                        }
                    }

                    portProtocolCounts.put(dstPortAndProtocolKey, portProtocolCounts.getOrDefault(dstPortAndProtocolKey, 0) + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error processing flow log from " + flowLogFile + ": " + e.getMessage());
        }
    }

    /**
     * Extracts the results of the tag and port/protocol counts to specified CSV files.
     *
     * @param tagCountsFileName the name of the file to save tag counts
     * @param portProtocolCountsFileName the name of the file to save port/protocol counts
     */
    public void extractResults(String tagCountsFileName, String portProtocolCountsFileName) {
        try (BufferedWriter tagWriter = new BufferedWriter(new FileWriter("output/" + tagCountsFileName + ".csv"));
             BufferedWriter portProtocolWriter = new BufferedWriter(new FileWriter("output/" + portProtocolCountsFileName + ".csv"))) {

            tagWriter.write("Tag,Count\n");
            for (Map.Entry<String, Integer> entry : tagCounts.entrySet()) {
                tagWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

            portProtocolWriter.write("Port_Protocol,Count\n");
            for (Map.Entry<String, Integer> entry : portProtocolCounts.entrySet()) {
                portProtocolWriter.write(entry.getKey() + "," + entry.getValue() + "\n");
            }

        } catch (IOException e) {
            System.err.println("An unexpected error occurred while printing results: " + e.getMessage());
        }
    }
}
