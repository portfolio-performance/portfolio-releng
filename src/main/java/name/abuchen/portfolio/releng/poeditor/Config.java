package name.abuchen.portfolio.releng.poeditor;

import java.io.File;

import com.thoughtworks.xstream.XStream;

public class Config
{
    private String task;
    private String token;
    private String projectId;

    private File sourceDirectory;
    private Translations translations;

    public static Config from(String[] args)
    {
        if (args.length != 5)
            throw new IllegalArgumentException(
                            "Usage: <task> <token> <projectId> <source directory> <translations.xml>");

        Config config = new Config();

        config.task = args[0];
        config.token = args[1];
        config.projectId = args[2];

        config.sourceDirectory = new File(args[3]);
        if (!config.sourceDirectory.exists() || !config.sourceDirectory.isDirectory())
            throw new IllegalArgumentException(args[3] + " does not exist or is not a directory");

        File translationsFile = new File(args[4]);
        if (!translationsFile.exists())
            throw new IllegalArgumentException(args[4] + " does not exist");

        XStream xstream = new XStream();
        xstream.allowTypesByWildcard(new String[] { "name.abuchen.portfolio.releng.**" });
        xstream.alias("translations", Translations.class);
        xstream.alias("artifact", Translations.Artifact.class);

        config.translations = (Translations) xstream.fromXML(translationsFile);

        return config;
    }

    private Config()
    {}

    public String getTask()
    {
        return task;
    }

    public String getToken()
    {
        return token;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public File getSourceDirectory()
    {
        return sourceDirectory;
    }

    public Translations getTranslations()
    {
        return translations;
    }
}
