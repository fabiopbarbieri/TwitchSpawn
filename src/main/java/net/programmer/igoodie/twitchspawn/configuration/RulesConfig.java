package net.programmer.igoodie.twitchspawn.configuration;

import com.google.common.io.Resources;
import net.programmer.igoodie.twitchspawn.TwitchSpawn;
import net.programmer.igoodie.twitchspawn.tslanguage.TSLRuleset;
import net.programmer.igoodie.twitchspawn.tslanguage.TSLRulesetCollection;
import net.programmer.igoodie.twitchspawn.tslanguage.parser.TSLSyntaxErrors;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RulesConfig {

    public static final Pattern PATTERN = Pattern.compile("^rules\\.(\\w+)\\.tsl$");
    public static final Pattern PATTERN_DISABLED = Pattern.compile("^rules\\.(\\w+)\\.tsl.disabled$");

    public static TSLRulesetCollection createRules(String directory) throws TSLSyntaxErrors {
        return new TSLRulesetCollection(
                create(directory + File.separator + "rules.default.tsl"),
                fromDirectory(new File(directory)));
    }

    private static TSLRuleset create(String filepath) throws TSLSyntaxErrors {
        return create(new File(filepath));
    }

    private static TSLRuleset create(File file) throws TSLSyntaxErrors {
        String script = "";

        try {
            // File is not there
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                script = defaultScript();

                FileUtils.writeStringToFile(file, script, StandardCharsets.UTF_8);
                TwitchSpawn.LOGGER.info("Saved default script to {}", file);

            } else { // File is there!
                script = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            }

        } catch (IOException e) {
            throw new InternalError("Tried to read from or save to a non-existing file");
        }

        return new TSLRuleset(script);
    }

    public static String readScript(String filepath) {
        try {
            return FileUtils.readFileToString(new File(filepath), StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new InternalError("Tried to read from a non-existing file -> " + filepath);
        }
    }

    private static String defaultScript() {
        try {
            URL location = Resources.getResource("assets/twitchspawn/default/rules.default.tsl");
            return Resources.toString(location, StandardCharsets.UTF_8);

        } catch (IOException e) {
            throw new InternalError("Missing default file: ../assets/twitchspawn/default/rules.default.tsl");
        }
    }

    private static List<TSLRuleset> fromDirectory(File directory) throws TSLSyntaxErrors {
        List<TSLRuleset> trees = new LinkedList<>();

        String directoryPath = directory.toString();


        for (String filename : directory.list()) {

            // skip disabled files
            if (filename.contains(".disabled")) {
                continue;
            }

            Matcher matcher = PATTERN.matcher(filename);

            if (matcher.find()) {
                String streamer = matcher.group(1);

                if (streamer.equalsIgnoreCase("default")) {
                    continue; // Default tree won't be included
                }

                TwitchSpawn.LOGGER.info("Loaded rule set for {} ({})", streamer, filename);
                String script = readScript(directoryPath + File.separator + filename);
                trees.add(new TSLRuleset(streamer, script));
            }
        }

        return trees;
    }

    /* ------------------------------------- */

    private RulesConfig() {
    } // Not instantiable

}
