# Flow Log Parser

## Overview
This project implements a program to parse flow log data and map each row to a corresponding tag based on a provided lookup table. The program reads flow logs and a lookup table from CSV files, processes the data, and generates output files that summarize tag counts and port/protocol combination counts.

## Requirements
- Java 17
  
## Input Files
1. **Flow Log File**: A plain text file containing flow log entries in the specified format. Each entry should be structured as follows:
   2 123456789012 eni-xxxx 10.0.1.xxx 198.51.100.xxx <dstPort> <srcPort> <protocolNumber> <otherFields> <startTime> <endTime> <action> OK

2. **Lookup Table**: A CSV file mapping destination ports and protocols to tags. The format should be:
   dstport,protocol,tag

3. **Protocol Numbers File**: A CSV file mapping protocol numbers to protocol names, structured as:
    protocolNumber,protocolName

## Output Files
The program generates two output CSV files:

1. **Tag Counts**: Contains counts of occurrences for each tag.
2. **Port/Protocol Counts**: Contains counts for each port/protocol combination.

## Running the Code
1. **Clone the Repository**:
```bash
git clone [<repository-url>](https://github.com/komal98/flow-log-analyzer.git)
cd illumio-flow-log-analyzer
```
2.Compile the Code: Navigate to the src directory and compile the code:
  ```bash
  cd src
  javac Main.java
  ```
3.Run the Program: Execute the program directly from the src directory:
  ```bash
  java Main
  ```
4. Ensure Input Files Are in Place: Make sure that the input files (flow-logs.txt, lookup.csv, and protocol-numbers.csv) are located in the src/input directory.

5. Check Output: After running, check the src/output directory for the generated CSV files containing the tag counts and port/protocol counts.



## Assumptions
- The program only supports the default log format (version 2) as specified.
- The input flow log file can be up to 10 MB in size.
- The lookup file can contain up to 10,000 mappings.
- Matches are case-insensitive.
- We assume that the data is clean and in the required format.
- The large flow log file used for testing is randomly generated; actual data should be used to test edge cases more effectively.
