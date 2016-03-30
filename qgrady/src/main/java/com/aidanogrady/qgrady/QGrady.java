package com.aidanogrady.qgrady;

import com.aidanogrady.qgrady.exceptions.*;
import com.aidanogrady.qgrady.syntax.*;
import com.aidanogrady.qgrady.syntax.Parser;
import org.apache.commons.cli.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * The entry point of the compiler. It handles the program arguments, to
 * ensure that they are all correct. It contains the entire high level program
 * structure and flow for the compiler, going through each of the stages that
 * produces the PRISM model.
 *
 * @author Aidan O'Grady
 * @since 0.1
 */
public class QGrady {

    /**
     * The argument options available for this system.
     */
    private Options options = createOptions();


    /**
     * Creates and returns the options for the program. There are four options
     * available to the user:
     * <ul>
     *     <li>file - the source Q'Grady file to be compiled.</li>
     *     <li>output - the destination PRISM file.</li>
     *     <li>help - displays the help dialogue to the user.</li>
     *     <li>version - shows program version history.</li>
     * </ul>
     *
     * @return options
     *
     */
    private Options createOptions() {
        Options options = new Options();
        options.addOption(Option.builder("f").longOpt("file").hasArg(true)
                .argName("file").required(true).desc("takes input from <file>")
                .build());
        options.addOption(Option.builder("o").longOpt("output").hasArg(true)
                .argName("file").desc("places output to <file>")
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
                Box box = parse(source);
                if(box != null)
                    codeGeneration(box, dest);
                else
                    System.out.println("Exiting system. Goodbye!");
            }
        } catch(ParseException e) {
            System.err.println("Parsing failed. Reason: " + e.getMessage());
        } catch (FileNotFoundException | InvalidFileTypeException e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * Performs syntax checking and semantic analysis on the given Q'Grady file.
     * This method determines whether we have been given a valid non-local box
     * that conforms to the restraints imposed upon it and returns said box.
     *
     * @param source  the Q'Grady file being compiled
     * @return non-local box extracted form source.
     */
    private Box parse(File source) {
        try {
            Parser p = new Parser(new Lexer(new FileReader(source.getPath())));
            Object result = p.parse().value;
            Box box = (Box) result;

            System.out.print("Checking variables... ");
            SemanticAnalyser.validateVariables(box);
            System.out.println("OK!");

            System.out.print("Checking values... ");
            SemanticAnalyser.validateValues(box.getProbs());
            System.out.println("OK!");

            System.out.print("Checking number of rows... ");
            SemanticAnalyser.validateRowAmount(box);
            System.out.println("OK!");

            System.out.print("Checking row lengths... ");
            SemanticAnalyser.validateRowLengths(box);
            System.out.println("OK!");

            System.out.print("Checking row sums... ");
            SemanticAnalyser.validateRowSums(box.getProbs());
            System.out.println("OK!");

            System.out.print("Checking for non-signalling... ");
            SemanticAnalyser.nonSignalling(box);
            System.out.println("OK!");
            return box;
        } catch (SignallingException | InvalidValueException |
                InvalidRowException | InvalidVariableException e) {
            System.out.println(e.getMessage());
        } catch (Error e) {
            String msg = e.getMessage().replace('<', '\'').replace('>', '\'');
            System.out.println("Cannot continue: " + msg);
        } catch (FileNotFoundException e) {
            System.out.println("Cannot continue, file missing.");
        } catch (Exception e) {
            System.out.println("Cannot continue, unknown error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Starts the file generation part of the compiler.
     *
     * @param box  the box being converted into .prism file.
     * @param dest  the .prism file to be written.
     */
    private void codeGeneration(Box box, File dest) {
        System.out.print("Writing box to " + dest.getName() + "... ");
        FileGenerator gen = new FileGenerator(box, dest);
        gen.generateLines();
        gen.write();
        System.out.println("OK!");
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
        System.out.println("qgrady (Q'Grady) v0.5.1");
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
                    input + "is not .qgrady file"
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
                    output + "is not .prism file"
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
        long start = System.currentTimeMillis();
        QGrady qGrady = new QGrady();
        qGrady.start(args);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
