import java.io.*;
import java.util.*;

public class FlowLogParser {
    private final Map<String, Integer> tagCounts = new HashMap<>();
    private final Map<String, Integer> portProtocolCounts = new HashMap<>();
    private final Map<String, Set<String>> lookupTable = new HashMap<>();
    private final Map<String, String> protocolNumbers = new HashMap<>();

    public void loadProtocolNumbers(String protocolNumbersFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(protocolNumbersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] protocolNumbersRow = line.split(",");
                if (protocolNumbersRow.length >= 2) {
                    protocolNumbers.put(protocolNumbersRow[0].trim(), protocolNumbersRow[1].trim().toLowerCase());
                }
            }
        }catch (IOException e) {
            System.err.println("Error loading protocol numbers from " + protocolNumbersFile + ": " + e.getMessage());
        }
    }

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
        }catch (IOException e) {
            System.err.println("Error loading lookup table from " + lookupFile + ": " + e.getMessage());
        }
    }

    public void processFlowLog(String flowLogFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(flowLogFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] processFlowLogRow= line.split(" ");
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


    public void extractResults(String tagCountsFileName, String portProtocolCountsFileName) {


        try (BufferedWriter tagWriter = new BufferedWriter(new FileWriter("src/output/" + tagCountsFileName + ".csv" ));
             BufferedWriter portProtocolWriter = new BufferedWriter(new FileWriter("src/output/" + portProtocolCountsFileName + ".csv"))) {

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


