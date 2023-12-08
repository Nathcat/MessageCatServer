import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.nathcat.RSA.KeyPair;
import com.nathcat.RSA.RSA;
import com.nathcat.messagecat_database_entities.User;
import com.nathcat.messagecat_server.RequestType;
import org.json.simple.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class GSONTest {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        Gson gson = new Gson();
        KeyPair pair = RSA.GenerateRSAKeyPair();

        String json = gson.toJson(pair);

        KeyPair pair2 = gson.fromJson(json, KeyPair.class);

        System.out.println(pair);
        System.out.println(pair2);
    }
}
