package name.abuchen.portfolio.releng.releasenotes;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class App
{

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
            throw new IllegalArgumentException("Usage: <domain> <appstream metadata>");

        var task = args[0];
        var metadataFile = new File(args[1]);
        if (!metadataFile.exists())
            throw new IllegalArgumentException(args[1] + " does not exist");

        var xmlContent = Files.readString(metadataFile.toPath());
        var document = parseDocument(xmlContent);

        var releaseNumber = extractReleaseNumber(document);
        var releaseDate = extractReleaseDate(document);
        List<String> germanBulletPoints = extractBulletPoints(document, "de");
        List<String> englishBulletPoints = extractBulletPoints(document, "en");

        switch (task)
        {
            case "plain":
                germanBulletPoints.forEach(s -> System.out.println("* " + s));
                System.out.println("\n---\n");
                englishBulletPoints.forEach(s -> System.out.println("* " + s));
                break;
            case "html":
                System.out.println("          <h5>" + releaseNumber + " / "
                                + releaseDate.format(
                                                DateTimeFormatter.ofPattern("dd. MMM yyyy").localizedBy(Locale.GERMANY))
                                + "</h5>");
                System.out.println("          <ul>");
                germanBulletPoints.forEach(s -> System.out.println("            <li>" + s + "</li>"));
                System.out.println("          </ul>");
                System.out.println("");
                break;
            case "embedded":
        }

    }

    private static Document parseDocument(String xmlContent)
                    throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlContent));
        return builder.parse(inputSource);
    }

    public static String extractReleaseNumber(Document document)
    {
        NodeList ulNodes = document.getElementsByTagName("release");
        return ((Element) ulNodes.item(0)).getAttribute("version");
    }

    public static LocalDate extractReleaseDate(Document document)
    {
        NodeList ulNodes = document.getElementsByTagName("release");
        return LocalDate.parse(((Element) ulNodes.item(0)).getAttribute("date"));
    }

    public static List<String> extractBulletPoints(Document document, String language)
    {
        List<String> bulletPoints = new ArrayList<>();

        NodeList ulNodes = document.getElementsByTagName("ul");
        for (int ii = 0; ii < ulNodes.getLength(); ii++)
        {
            Element ulElement = (Element) ulNodes.item(ii);

            String lang = ulElement.getAttribute("xml:lang");
            if (lang.equals(language))
            {
                NodeList liNodes = ulElement.getElementsByTagName("li");
                for (int j = 0; j < liNodes.getLength(); j++)
                {
                    Element liElement = (Element) liNodes.item(j);
                    String bulletPoint = liElement.getTextContent().trim();
                    bulletPoints.add(bulletPoint);
                }
                break;
            }
        }
        return bulletPoints;
    }
}
