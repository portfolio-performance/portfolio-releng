package name.abuchen.portfolio.releng.poeditor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UploadNewTermsTask implements Task
{

    @Override
    public void perform(Config config, TermData data) throws IOException
    {
        POEditorAPI api = new POEditorAPI(config);

        Set<Term> remoteTerms = new HashSet<>(api.getTerms());
        Set<Term> localTerms = data.getLocalTerms();

        Set<Term> newTerms = new HashSet<>(localTerms);
        newTerms.removeAll(remoteTerms);

        if (newTerms.isEmpty())
            return;

        api.addTerms(newTerms);

        UploadTranslationsTask upload = new UploadTranslationsTask();

        upload.perform(config, newTerms, data);
    }

}
