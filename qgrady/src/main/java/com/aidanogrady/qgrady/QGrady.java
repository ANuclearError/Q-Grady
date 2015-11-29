package com.aidanogrady.qgrady;

import org.apache.commons.cli.*;

/**
 * The entry point of the compiler. It handles the program arguments, to
 * ensure that they are all correct.
 *
 * TODO: Implement option groups to create system I want.
 *
 * @author Aidan O'Grady
 * @since 1.0
 */
public class QGrady {

    /**
     * The main method parses the args, and begins the execution of the
     * compiler.
     *
     * @param args - program args.
     */
    public static void main(String[] args) throws ParseException {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            parseOptions(line, options);
        } catch(ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        }
    }

    /**
     * Creates and returns the options for the program.
     * @return options
     */
    private static Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("f").longOpt("file").hasArg(true)
                .argName("file").required(true).desc("places output to <file>")
                .build());
        options.addOption(Option.builder("o").longOpt("output").hasArg(true)
                .argName("file").desc("takes input from <file>")
                .build());
        options.addOption(Option.builder("h").longOpt("help")
                .desc("prints this message").build());
        options.addOption(Option.builder("v").longOpt("version")
                .desc("displays compiler information version").build());
        return options;
    }

    /**
     * Handles the parsing of the user's options, ensuring the heirarchy of
     * calls.
     * @param line - the options the user has input
     * @param options - the options the system looks for.
     */
    private static void parseOptions(CommandLine line, Options options) {
        if(line.hasOption("h")) {   // Display only help information
            HelpFormatter formatter = new HelpFormatter();
            formatter.setOptionComparator(null);
            formatter.printHelp("qgrady", options, true);
        } else if(line.hasOption("v")) { // Display only version info.
            System.out.println("qgrady (Q'Grady) v1.0");
            System.out.println("Author: Aidan O'Grady");
            System.out.println("4th Year project for M. Eng. Computer Science" +
                    " at the University of Strathclyde, Glasgow");
        } else { // Move onto actually compiling.
            System.out.println("Input file: " + line.getOptionValue("f"));
            if(line.hasOption("o")) { // User specified output file.
                System.out.println("Output file: " + line.getOptionValue("o"));
            }
        }
    }
}
