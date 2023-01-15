package name.abuchen.portfolio.releng.poeditor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.essiembre.eclipse.rbe.model.bundle.BundleEntry;
import com.essiembre.eclipse.rbe.model.bundle.BundleGroup;

public class POEditorAPI
{
    private final Config config;
    private final HttpClient client;

    public POEditorAPI(Config config)
    {
        this.config = config;
        this.client = HttpClientBuilder.create().build();
    }

    public List<Term> getTerms() throws IOException
    {
        HttpPost post = new HttpPost("https://api.poeditor.com/v2/terms/list");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("api_token", config.getToken()));
        urlParameters.add(new BasicNameValuePair("id", config.getProjectId()));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());

        JSONObject responseData = (JSONObject) JSONValue.parse(body);
        if (responseData == null)
            throw new IOException("responseBody"); //$NON-NLS-1$

        JSONObject result = (JSONObject) responseData.get("result"); //$NON-NLS-1$
        if (result == null)
            throw new IOException("result"); //$NON-NLS-1$

        JSONArray terms = (JSONArray) result.get("terms"); //$NON-NLS-1$
        if (terms == null || terms.isEmpty())
            return Collections.emptyList();

        List<Term> answer = new ArrayList<>();

        for (Object jsonTerm : terms)
        {
            JSONObject t = (JSONObject) jsonTerm;

            answer.add(new Term((String) t.get("term"), (String) t.get("context")));
        }

        return answer;
    }

    public void deleteTerms(Collection<Term> terms) throws IOException
    {
        String data = toJsonString(terms);

        HttpPost post = new HttpPost("https://api.poeditor.com/v2/terms/delete");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("api_token", config.getToken()));
        urlParameters.add(new BasicNameValuePair("id", config.getProjectId()));
        urlParameters.add(new BasicNameValuePair("data", data));

        post.addHeader("Accept", "application/json");

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
    }

    public void addTerms(Collection<Term> terms) throws IOException
    {
        String data = toJsonString(terms);

        HttpPost post = new HttpPost("https://api.poeditor.com/v2/terms/add");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("api_token", config.getToken()));
        urlParameters.add(new BasicNameValuePair("id", config.getProjectId()));
        urlParameters.add(new BasicNameValuePair("data", data));

        post.addHeader("Accept", "application/json");

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());
        System.out.println(body);
    }

    public void uploadTranslations(Collection<Term> terms, Map<String, BundleGroup> context2group, Language language)
                    throws IOException
    {
        String data = toJsonString(terms, context2group, language);

        HttpPost post = new HttpPost("https://api.poeditor.com/v2/projects/upload");

        HttpEntity entity = MultipartEntityBuilder.create() //
                        .addTextBody("api_token", config.getToken()) //
                        .addTextBody("id", config.getProjectId()) //
                        .addTextBody("updating", "translations") //
                        .addTextBody("language", language.getPoeditor()) //
                        .addTextBody("overwrite", "1")
                        .addBinaryBody("file", data.getBytes(StandardCharsets.UTF_8.name()),
                                        ContentType.APPLICATION_JSON, "data.json")
                        .build();

        post.addHeader("Accept", "application/json");

        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());
        System.out.println(language.getIdentifier() + ": " + body);
    }

    public List<TranslatedTerm> downloadTranslations(Language language) throws IOException
    {
        HttpPost post = new HttpPost("https://api.poeditor.com/v2/projects/export");

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("api_token", config.getToken()));
        urlParameters.add(new BasicNameValuePair("id", config.getProjectId()));
        urlParameters.add(new BasicNameValuePair("language", language.getPoeditor()));
        urlParameters.add(new BasicNameValuePair("type", "json"));
        urlParameters.add(new BasicNameValuePair("filters", "translated"));

        post.addHeader("Accept", "application/json");

        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());
        System.out.println(language.getIdentifier() + ": " + body);

        JSONObject responseData = (JSONObject) JSONValue.parse(body);
        if (responseData == null)
            throw new IOException("responseBody"); //$NON-NLS-1$

        JSONObject result = (JSONObject) responseData.get("result"); //$NON-NLS-1$
        if (result == null)
            throw new IOException("result"); //$NON-NLS-1$

        String url = (String) result.get("url");

        return doDownload(url);

    }

    private List<TranslatedTerm> doDownload(String url) throws IOException
    {
        HttpGet get = new HttpGet(url);

        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            throw new IOException(response.getStatusLine().toString());

        String body = EntityUtils.toString(response.getEntity());

        JSONArray responseData = (JSONArray) JSONValue.parse(body);
        if (responseData == null || responseData.isEmpty())
            throw new IOException("responseBody"); //$NON-NLS-1$

        List<TranslatedTerm> answer = new ArrayList<>();

        for (Object jsonTerm : responseData)
        {
            JSONObject t = (JSONObject) jsonTerm;

            answer.add(new TranslatedTerm((String) t.get("term"), (String) t.get("context"),
                            (String) t.get("definition"), (String) t.get("comment")));
        }

        return answer;
    }

    @SuppressWarnings("unchecked")
    private String toJsonString(Collection<Term> terms, Map<String, BundleGroup> context2group, Language language)
    {
        JSONArray array = new JSONArray();

        for (Term term : terms)
        {
            BundleGroup bundleGroup = context2group.get(term.getContext());
            if (bundleGroup == null)
                continue;

            BundleEntry entry = bundleGroup.getBundleEntry(language.getLocale(), term.getTerm());
            if (entry == null)
                continue;

            JSONObject t = new JSONObject();
            t.put("term", term.getTerm());
            t.put("context", term.getContext());
            t.put("definition", entry.getValue());

            if (!entry.getComment().isEmpty())
                t.put("comment", entry.getComment());

            array.add(t);
        }

        return array.toJSONString();
    }

    @SuppressWarnings("unchecked")
    private String toJsonString(Collection<Term> terms)
    {
        JSONArray array = new JSONArray();

        List<Term> sorted = new ArrayList<>(terms);
        Collections.sort(sorted, (r, l) -> {
            int compare = r.getContext().compareTo(l.getContext());
            return compare != 0 ? compare : r.getTerm().compareTo(l.getTerm());
        });

        for (Term term : sorted)
        {
            JSONObject t = new JSONObject();
            t.put("term", term.getTerm());
            t.put("context", term.getContext());
            array.add(t);
        }

        return array.toJSONString();
    }
}
