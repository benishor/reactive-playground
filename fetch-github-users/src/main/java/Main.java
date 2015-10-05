import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import github.User;
import org.json.JSONArray;
import org.json.JSONObject;
import rx.Observable;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Adrian Scripca
 */
public class Main {

    public static void main(String[] args) throws UnirestException, IOException {
        getUserList()
                .skip(0)
                .take(3)
                .map(User::getReposUrl)
                .flatMap(Main::getJsonArrayFromUrl)
                .filter(o -> o.getBoolean("has_issues") && o.getInt("open_issues_count") > 0)
                .map(o -> o.getString("url"))
                .flatMap(Main::getJsonArrayFromUrl)
                .subscribe(System.out::println)
        ;
    }

    private static Observable<JSONObject> getJsonArrayFromUrl(String url) {
        try {
            List<JSONObject> itemList = new LinkedList<>();
            JSONArray array = Unirest.get(url).asJson().getBody().getArray();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = (JSONObject) array.get(i);
                itemList.add(o);
            }
            return Observable.from(itemList);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    private static Observable<User> getUserList() throws UnirestException, IOException {
        String body = Unirest.get("https://api.github.com/users").asString().getBody();
        List<User> userList = new ObjectMapper().readValue(body, new TypeReference<List<User>>() {
        });
        return Observable.from(userList);
    }
}
