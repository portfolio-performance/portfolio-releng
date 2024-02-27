
package name.abuchen.portfolio.releng.poeditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.XPropertiesGenerator;

public class DownloadTranslationsTask implements Task
{

    @Override
    public void perform(Config config, TermData data) throws IOException
    {
        POEditorAPI api = new POEditorAPI(config);

        Language defaultLanguage = config.getTranslations().getDefaultLanguage();
        save(config, data, api, defaultLanguage);

        for (Language language : config.getTranslations().getLanguages())
        {
            save(config, data, api, language);
        }
    }

    private void save(Config config, TermData data, POEditorAPI api, Language language) throws IOException
    {
        List<TranslatedTerm> translatedTerms = api.downloadTranslations(language);

        final Locale locale = language.getLocale();

        for (TranslatedTerm term : translatedTerms)
        {
            BundleGroup bundleGroup = data.getContext2group().get(term.getContext());
            if (bundleGroup == null)
            {
                System.err.println("Context not found: " + term);
                continue;
            }

            // remove leading and trailing white space
            String value = fixApostrophes(trim(term.getDefinition()));

            bundleGroup.addBundleEntry(locale, new BundleEntry(term.getTerm(), value, null));
        }

        for (Map.Entry<String, BundleGroup> entry : data.getContext2group().entrySet())
        {
            Bundle bundle = entry.getValue().getBundle(locale);
            if (bundle == null)
                continue;

            String content = XPropertiesGenerator.generate(bundle);
            if (content.isEmpty())
                continue;

            Translations.Artifact artifact = config.getTranslations().getArtifact(entry.getKey());

            String path = artifact.getPath() + File.separator + artifact.getFilename();

            if (!config.getTranslations().getDefaultLanguage().equals(language))
                path = path + "_" + locale;

            File bundleFile = new File(config.getSourceDirectory(), path + ".properties");

            Files.write(bundleFile.toPath(), content.getBytes(StandardCharsets.UTF_8.name()), StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public static String fixApostrophes(String input)
    {
        // fix the apostrophe only if the label is (most likely) used by
        // MessageFormat, i.e. if it contains a placeholder indicated by a curly
        // brace
        if (input == null || !input.contains("{"))
            return input;

        StringBuilder modifiedString = new StringBuilder();
        for (int ii = 0; ii < input.length(); ii++)
        {
            char currentChar = input.charAt(ii);

            // check if the current character is a single quote
            if (currentChar == '\'')
            {
                modifiedString.append("''");

                // skip next character if we have double quotes already
                if (ii + 1 < input.length() && input.charAt(ii + 1) == '\'')
                {
                    ii++;
                }
            }
            else
            {
                modifiedString.append(currentChar);
            }
        }

        return modifiedString.toString();
    }

    /**
     * Since {@see String#trim} does not trim all whitespace and space
     * characters, this is an alternative implementation. Inspired by the blog
     * post at http://closingbraces.net/2008/11/11/javastringtrim/
     */
    private String trim(String value)
    {
        if (value == null)
            return null;

        int len = value.length();
        int st = 0;

        while ((st < len) && isWhitespace(value.charAt(st)))
        {
            st++;
        }

        while ((st < len) && Character.isWhitespace(value.charAt(len - 1)))
        {
            len--;
        }
        return ((st > 0) || (len < value.length())) ? value.substring(st, len) : value;

    }

    private boolean isWhitespace(char c)
    {
        if (Character.isWhitespace(c) || Character.isSpaceChar(c))
            return true;

        return c == '\uFEFF'; // zero width no-break space
    }
}
