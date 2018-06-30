package name.abuchen.portfolio.releng.poeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.essiembre.eclipse.rbe.model.bundle.Bundle;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;
import com.essiembre.eclipse.rbe.model.bundle.XPropertiesParser;

public class TermData
{
    private Set<Term> localTerms = new HashSet<>();
    private Map<String, BundleGroup> context2group;

    public static TermData create(Config config) throws FileNotFoundException
    {
        Set<Term> localTerms = new HashSet<>();
        Map<String, BundleGroup> context2group = new HashMap<>();

        for (Translations.Artifact artifact : config.getTranslations().getArtifacts())
        {
            BundleGroup bundleGroup = new BundleGroup();

            context2group.put(artifact.getContext(), bundleGroup);

            File file = new File(config.getSourceDirectory(),
                            artifact.getPath() + File.separator + artifact.getFilename() + ".properties");

            bundleGroup.addBundle(config.getTranslations().getDefaultLanguage(), load(file));

            List<Locale> languages = config.getTranslations().getLanguages();
            for (Locale language : languages)
            {
                File bundleFile = new File(config.getSourceDirectory(), artifact.getPath() + File.separator
                                + artifact.getFilename() + "_" + language.getLanguage() + ".properties");

                if (!bundleFile.exists())
                {
                    Bundle bundle = new Bundle();
                    bundleGroup.addBundle(language, bundle);
                }
                else
                {
                    Bundle bundle = load(bundleFile);
                    bundleGroup.addBundle(language, bundle);
                }
            }

            bundleGroup.getKeys().forEach(key -> localTerms.add(new Term(key, artifact.getContext())));
        }

        return new TermData(localTerms, context2group);
    }

    private static Bundle load(File file) throws FileNotFoundException
    {
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name()))
        {
            String properties = scanner.useDelimiter("\\A").next();
            return XPropertiesParser.parse(properties);
        }
    }

    private TermData(Set<Term> localTerms, Map<String, BundleGroup> context2group)
    {
        this.localTerms = localTerms;
        this.context2group = context2group;
    }

    public Map<String, BundleGroup> getContext2group()
    {
        return context2group;
    }

    public Set<Term> getLocalTerms()
    {
        return localTerms;
    }
}
