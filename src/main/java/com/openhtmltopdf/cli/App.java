package com.openhtmltopdf.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import com.openhtmltopdf.outputdevice.helper.ExternalResourceControlPriority;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.util.XRLog;

import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.w3c.dom.Document;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

public class App
{
    @Command(
       name = "convert",
       description = "Converts a single html file into a PDF"
    )
    public static class Run implements Callable<Integer> {
        @Option(
            names = { "--input", "-i" },
            description = "The html input file",
            paramLabel = "<input>",
            required = true
        )
        File input;

        @Option(
            names = { "--output", "-o" },
            description = "The PDF output file",
            paramLabel = "<output>",
            required = true
        )
        File output;

        @Option(
            names = { "--xhtml", "-x" },
            description = "Use to specify that the input file is valid XHTML (skipping the HTML to XHTML step)"
        )
        boolean xhtml = false;

        @Option(
            names = { "--verbose", "-v" },
            description = "Verbose logging"
        )
        boolean verbose = false;

        @Option(
            names = { "--quiet", "-q" },
            description = "Quiet logging"
        )
        boolean quiet = false;

        @Option(
            names = { "--block", "-b" },
            description = "Block linked resources (CSS, images, fonts)"
        )
        boolean block;

        @Override
        public Integer call() throws Exception {
            if (quiet && !verbose) {
                XRLog.listRegisteredLoggers().forEach(logger -> XRLog.setLevel(logger, Level.OFF));
            }
            if (!verbose && !quiet) {
                XRLog.listRegisteredLoggers().forEach(logger -> XRLog.setLevel(logger, Level.WARNING));
            }

            long timeStart = System.currentTimeMillis();
            if (!quiet) {
                System.out.println("Attempting to convert '" + input.getAbsolutePath() + "' to PDF at '" + output.getAbsolutePath() + "'");
            }

            try (FileOutputStream os = new FileOutputStream(output)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();

                if (block) {
                    builder.useUriResolver((base, rel) -> null);
                    builder.useExternalResourceAccessControl((uri, type) -> false, ExternalResourceControlPriority.RUN_BEFORE_RESOLVING_URI);
                    builder.useExternalResourceAccessControl((uri, type) -> false, ExternalResourceControlPriority.RUN_AFTER_RESOLVING_URI);
                }

                if (!xhtml) {
                    org.jsoup.nodes.Document jsoup = Jsoup.parse(input, "UTF-8");
                    Document doc = new W3CDom().fromJsoup(jsoup);
                    builder.withW3cDocument(doc, input.getAbsoluteFile().toURI().toURL().toExternalForm());
                } else {
                    builder.withFile(input);
                }

                builder.toStream(os);
                builder.useFastMode();
                builder.run();

                if (!quiet) {
                    System.out.println("Successfully created PDF in " + (System.currentTimeMillis() - timeStart) + "ms");
                }

                return 0;
            }
        }
    }


   @Command(
       mixinStandardHelpOptions = true,
       subcommands = { Run.class, CommandLine.HelpCommand.class },
       version = "openhtmltopdf-cli 1.0.10")
   public static class Cli {
   }

    public static void main( String[] args ) {
        int res = new CommandLine(new Cli()).execute(args);
        System.exit(res);
    }
}
