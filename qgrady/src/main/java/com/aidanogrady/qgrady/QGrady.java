package com.aidanogrady.qgrady;

import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * The entry point of the compiler. It handles the program arguments, to
 * ensure that they are all correct.
 *
 * @author Aidan O'Grady
 * @since 0.0
 */
public class QGrady {

    /**
     * The argument options available for this system.
     */
    private Options options;

    public QGrady() {
        options = createOptions();
    }


    /**
     * Creates and returns the options for the program.
     * @return options
     */
    private Options createOptions() {
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
     * Starts the system execution by parsing the user's arguments so that the
     * control can be dictated.
     *
     * @param args - program arguments
     */
    public void start(String[] args) {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            if(line.hasOption("h")) {
                help();
            }
            else if (line.hasOption("v")) {
                version();
            }
            else {
                String input = line.getOptionValue("f");
                String output = line.getOptionValue("o");
                File source = validateInput(input);
                File dest = validateOutput(output, input);
                System.out.println(source.getPath());
                System.out.println(dest.getPath());
            }
        } catch(ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (InvalidFileTypeException e) {
            System.err.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Displays the help message associated with the usage of the arguments.
     */
    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setOptionComparator(null);
        formatter.printHelp("qgrady", options, true);
    }


    /**
     * Displays the version information of the software.
     */
    private void version() {
        System.out.println("qgrady (Q'Grady) v0.0");
        System.out.println("Author: Aidan O'Grady");
        System.out.println("4th Year project for M. Eng. Computer Science" +
                " at the University of Strathclyde, Glasgow");
    }


    /**
     * Given the location of the input, returns the File the input locates if it
     * is valid.
     *
     * @param input the location of the input file.
     * @return input file
     * @throws FileNotFoundException - If the input file does not exist.
     * @throws InvalidFileTypeException - If the input file is not right.
     */
    private File validateInput(String input) throws FileNotFoundException,
            InvalidFileTypeException
    {
        // We must ensure the file exists.
        File source = new File(input);
        if(!source.exists()) {
            throw new FileNotFoundException(input + ": no such file.");
        }

        // We must ensure that the file is a valid Q'Grady file.
        String extension = FilenameUtils.getExtension(input);
        if(source.isDirectory() || !extension.equals("qgrady")) {
            throw new InvalidFileTypeException(
                    input + ": not recognized as .qgrady file"
            );
        }

        return source;
    }


    /**
     * Given the location of the user's desired destination .prism file, returns
     * the file if found. If the user did not provide an option, the given
     * input location is used to create a destination location instead.
     *
     * @param output the location of the user's desired destination.
     * @param input the location of the input file for if there is no output.
     * @return the output file
     * @throws FileNotFoundException - If the input file does not exist.
     * @throws InvalidFileTypeException - If the input file is not right.
     */
    private File validateOutput(String output, String input) throws
            FileNotFoundException, InvalidFileTypeException
    {
        // If the user did not provide a destination file, we will create one.
        if(output == null) {
            output = FilenameUtils.removeExtension(input);
            output += ".prism";
        }

        // We must ensure that the destination is not a directory and is a prism
        // file.
        File dest = new File(output);
        String extension = FilenameUtils.getExtension(output);
        if(dest.isDirectory() || !extension.equals("prism")) {
            throw new InvalidFileTypeException(
                    output + ": not recognized as .prism file"
            );
        }

        return dest;
    }


    /**
     * The main method parses the args, and begins the execution of the
     * compiler.
     *
     * @param args - program args.
     */
    public static void main(String[] args) throws ParseException {
        QGrady qGrady = new QGrady();
        qGrady.start(args);
    }
}
