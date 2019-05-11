package edu.uw.csep.scs;

import java.io.PrintWriter;
import org.apache.commons.cli.*;


public class Main {

    public static void main(String[] args) throws ParseException {
        
        Options options = new Options();

        Option help = new Option("h", false, "options");
        options.addOption(help);

        Option input = new Option("i",true,"input file path");
        options.addOption(input);

        Option modify = new Option("m",false,"modify files");
        options.addOption(modify);

        Option checkJavadoc = new Option("j",false,"check javadoc style");
        options.addOption(checkJavadoc);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("h")) {
            showOptions(options, "");
            return;
        }

        String filePath = cmd.getOptionValue("i");

        if (cmd.hasOption("j")) {
            System.out.println("TODO: Check javadoc...");
        }

    }

    private static void showOptions(Options options, String message) {
        PrintWriter printWriter = new PrintWriter(System.out);
        printWriter.println(message);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(printWriter, 80, "ls", "",
                options, 4, 4,"", true);
        printWriter.flush();
    }
}
