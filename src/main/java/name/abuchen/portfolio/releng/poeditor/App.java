package name.abuchen.portfolio.releng.poeditor;

public class App
{
    public static void main(String[] args) throws Exception
    {
        Config config = Config.from(args);

        TermData data = TermData.create(config);

        Task task;

        switch (config.getTask())
        {
            case "sync-terms":
                task = new SyncTermsTask();
                break;
            case "upload-new-terms":
                task = new UploadNewTermsTask();
                break;
            case "upload":
                task = new UploadTranslationsTask();
                break;
            case "download":
                task = new DownloadTranslationsTask();
                break;
            default:
                throw new IllegalArgumentException(config.getTask());
        }

        task.perform(config, data);
    }
}
