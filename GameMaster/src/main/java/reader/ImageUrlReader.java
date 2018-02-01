package reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;

import de.maxkroner.values.Keys;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ImageUrlReader {
	private static final String cx = "014357723537713710519:earmz8ohk6k"; //custom google search id
	private static final String url_pattern = "https://www.googleapis.com/customsearch/v1?key=%s&cx=%s&q=%s&start=%d&searchType=image&fileType=\"jpg%%20png\"";
	private static final OkHttpClient client = new OkHttpClient();

	/**
	 * Trys to get a list of image-urls for the specified search-term.
	 * @param term to use for the image search
	 * @param start index of the first result to return, starting with 1
	 * @return list of Strings if successful or else an empty list
	 */
	public static List<String> getImageUrlsForTerm(String term, int start) {
		
		try{	
			
			String url = String.format(url_pattern, Keys.google_api_key, cx, term, start);
		
			return httpGet(url) //get HTTP Result as Optional<String>
							.map(JSONObject::new) //get JSON Object from JSON
							.flatMap(T -> Optional.of(T.optJSONArray("items"))).map(JSONArray::toList) //List of Objects in "items"
							.map(T -> T.stream().map(Z -> (String) ((HashMap) Z).get("link")) //get link as String from every item
							.collect(Collectors.toList())).orElse(Collections.emptyList());
		
		} catch (Exception e){
			Logger.error(e);
			return Collections.emptyList();
		}
			
	}

	public static List<String> getImageUrlsForTerm(String term) {
		return getImageUrlsForTerm(term, 1);
	}

	private static Optional<String> httpGet(String url) {
		Request request = new Request.Builder().url(url).build();

		try (Response response = client.newCall(request).execute()) {
			return Optional.of(response.body().string());
		} catch (IOException e) {
			return Optional.empty();
		}
	}

}
