package name.abuchen.portfolio.releng.poeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Translations
{
    public class Artifact
    {
        private String path;
        private String filename;
        private String context;

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
    }

    private String defaultLanguage;
    private List<String> languages = new ArrayList<>();
    private List<Artifact> artifacts = new ArrayList<>();

    public Locale getDefaultLanguage()
    {
        return Locale.forLanguageTag(defaultLanguage);
    }

    public List<Locale> getLanguages()
    {
        return languages.stream().map(Locale::new).collect(Collectors.toList());
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
