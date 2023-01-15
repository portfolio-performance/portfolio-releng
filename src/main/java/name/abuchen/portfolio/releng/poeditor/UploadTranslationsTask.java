package name.abuchen.portfolio.releng.poeditor;

import java.io.IOException;
import java.util.Set;

public class UploadTranslationsTask implements Task
{

    @Override
    public void perform(Config config, TermData data) throws IOException
    {
        perform(config, data.getLocalTerms(), data);
    }

    public void perform(Config config, Set<Term> terms, TermData data) throws IOException
    {
        POEditorAPI api = new POEditorAPI(config);

        api.uploadTranslations(terms, data.getContext2group(), config.getTranslations().getDefaultLanguage());

        for (Language language : config.getTranslations().getLanguages())
        {
            try
            {
                Thread.sleep(30 * 1000L);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            api.uploadTranslations(terms, data.getContext2group(), language);
        }
    }

}
