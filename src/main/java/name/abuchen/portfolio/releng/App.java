package name.abuchen.portfolio.releng;

import java.util.Arrays;

public class App
{
    public static void main(String[] args) throws Exception
    {
        if (args.length < 1)
            throw new IllegalArgumentException("Usage: <domain> ...");

        String domain = args[0];

        switch (domain)
        {
            case "poeditor":
                name.abuchen.portfolio.releng.poeditor.App.main(Arrays.copyOfRange(args, 1, args.length));
                break;
            case "release-notes":
                name.abuchen.portfolio.releng.releasenotes.App.main(Arrays.copyOfRange(args, 1, args.length));
                break;
        }
    }
}
