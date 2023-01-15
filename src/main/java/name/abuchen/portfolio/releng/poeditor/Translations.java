package name.abuchen.portfolio.releng.poeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Translations
{
    public class Artifact
    {
        private String path;
        private String filename;
        private String context;
        private List<String> excludes = new ArrayList<>();

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
        }

        public String getFilename()
        {
            return filename;
        }

        public void setFilename(String filename)
        {
            this.filename = filename;
        }

        public String getContext()
        {
            return context;
        }

        public void setContext(String context)
        {
            this.context = context;
        }

        public Predicate<String> getFilter()
        {
            if (excludes == null || excludes.isEmpty())
                return key -> true;

            List<Pattern> pattern = excludes.stream().map(Pattern::compile).collect(Collectors.toList());

            return key -> {
                for (Pattern p : pattern)
                {
                    if (p.matcher(key).matches())
                        return false;
                }
                return true;
            };
        }
    }

    private Language defaultLanguage;
    private List<Language> languages = new ArrayList<>();
    private List<Artifact> artifacts = new ArrayList<>();

    public Language getDefaultLanguage()
    {
        return defaultLanguage;
    }

    public List<Language> getLanguages()
    {
        return languages;
    }

    public List<Artifact> getArtifacts()
    {
        return artifacts;
    }

    public Artifact getArtifact(String context)
    {
        return artifacts.stream().filter(a -> context.equals(a.getContext())).findFirst()
                        .orElseThrow(IllegalArgumentException::new);
    }
}
