
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

            bundleGroup.addBundleEntry(locale, new BundleEntry(term.getTerm(), term.getDefinition(), null));
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

}
