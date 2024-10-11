public class Main {
    static FlowLogParser parser = new FlowLogParser();
    public static void main(String[] args) {
        testBasicFunctionality();
    }

    private static void testBasicFunctionality(){
        parser.loadProtocolNumbers("src/input/protocol-numbers.csv");
        parser.loadLookupTable("src/input/lookup-basic.csv");
        parser.processFlowLog("src/input/flow-logs-basic.txt");
        parser.extractResults("basicTagCounts", "basicPortProtocolCounts");
    }
}
