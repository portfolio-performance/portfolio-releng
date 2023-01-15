package name.abuchen.portfolio.releng.poeditor;

import java.util.Locale;

public class Language
{
    private String identifier;
    private String poeditor;

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public String getPoeditor()
    {
        return poeditor;
    }

    public void setPoeditor(String poeditor)
    {
        this.poeditor = poeditor;
    }

    public Locale getLocale()
    {
        return Locale.forLanguageTag(identifier);
    }

}
