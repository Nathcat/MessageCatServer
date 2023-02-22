import com.nathcat.RSA.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import com.nathcat.messagecat_server.*;
import com.nathcat.messagecat_database_entities.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

public class TestProgram {
    private static class TestData {
        public final String name;
        public final Object input;
        public final Object expectedOutput;

        public TestData(String name, Object input, Object expectedOutput) {
            this.name = name;
            this.input = input;
            this.expectedOutput = expectedOutput;
        }

        public boolean test(Object output) {
            if (output == null) {
                return expectedOutput == null;
            }

            if (output.getClass().isArray()) {
                return Arrays.equals((Object[]) output, (Object[]) expectedOutput);
            }
            return output.equals(expectedOutput);
        }

        @Override
        public String toString() {
            String result = "Test \"" + this.name + "\" {\n";

            if (input == null) {
                result += "  input: null";
            }
            else {
                result += "  input: " + (input.getClass().isArray() ? Arrays.toString((Object[]) input) : input);
            }

            if (expectedOutput == null) {
                result += "\n\n  Expected output: null";
            }
            else {
                result += "\n\n  Expected output: " + (expectedOutput.getClass().isArray() ? Arrays.toString((Object[]) expectedOutput) : expectedOutput);
            }

            return result += "\n}";
        }
    }

    private static final String date = new Date().toString();
    private static final JSONParser jsonParser = new JSONParser();

    private static final TestData[] tests = new TestData[] {
            new TestData(
                    "AddUser",
                    makeJSONObject(new String[] {"type", "data"}, new Object[] {RequestType.AddUser, new User(-1, "12345", "Oogle", "Herman", date, "default.png")}),
                    new User(1, "12345", "Oogle", "Herman", date, "default.png")
                    ),


            new TestData(
                    "Auth none correct",
                    makeJSONObject(new String[] {"type", "data"}, new Object[] {RequestType.Authenticate, new User(-1, "dwiejfef", "adiwdjiwad", null, null, null)}),
                    "failed"
                    ),

            new TestData(
                    "Auth username correct",
                    makeJSONObject(new String[] {"type", "data"}, new Object[] {RequestType.Authenticate, new User(-1, "12345", "adiwdjiwad", null, null, null)}),
                    "failed"
                    ),

            new TestData(
                    "Auth all correct",
                    makeJSONObject(new String[] {"type", "data"}, new Object[] {RequestType.Authenticate, new User(-1, "12345", "Oogle", null, null, null)}),
                    new User(1, "12345", "Oogle", "Herman", date, "default.png")
                    ),


            new TestData(
                    "GetUser by ID, no results",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "id", new User(5, null, null, null, null, null)}),
                    null
                    ),

            new TestData(
                    "GetUser by ID, result",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "id", new User(1, null, null, null, null, null)}),
                    new User(1, "12345", null, "Herman", date, "default.png")
                    ),

            new TestData(
                    "GetUser by username, no result",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "username", new User(-1, "iaoaidj", null, null, null, null)}),
                    null
                    ),

            new TestData(
                    "GetUser by username, result",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "username", new User(-1, "12345", null, null, null, null)}),
                    new User(1, "12345", null, "Herman", date, "default.png")
                    ),

            new TestData(
                    "GetUser by display name, no result",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "displayName", new User(-1, null, null, "dkdowo", null, null)}),
                    new User[0]
                    ),

            new TestData(
                    "GetUser by display name, result",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "displayName", new User(-1, null, null, "Herman", null, null)}),
                    new User[] {new User(1, "12345", null, "Herman", date, "default.png")}
                    ),

            new TestData(
                    "GetUser by display name, result, shorter input",
                    makeJSONObject(new String[] {"type", "selector", "data"}, new Object[] {RequestType.GetUser, "displayName", new User(-1, null, null, "Her", null, null)}),
                    new User[] {new User(1, "12345", null, "Herman", date, "default.png")}
                    ),
    };

    private static boolean[] testResults;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, PrivateKeyException, PublicKeyException {
        Socket mainSock = new Socket("localhost", 1234);
        ObjectOutputStream oos = new ObjectOutputStream(mainSock.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(mainSock.getInputStream());

        KeyPair keyPair = RSA.GenerateRSAKeyPair();
        KeyPair serverKeyPair = (KeyPair) ois.readObject();
        oos.writeObject(new KeyPair(keyPair.pub, null));
        oos.flush();

        int handlerID = (int) keyPair.decrypt((EncryptedObject) ois.readObject());
        int lrSockPort = (int) keyPair.decrypt((EncryptedObject) ois.readObject());

        Socket lrSock = new Socket("localhost", lrSockPort);
        ObjectInputStream lrOis = new ObjectInputStream(lrSock.getInputStream());

        testResults = new boolean[tests.length];

        for (int i = 0; i < tests.length; i++) {
            System.out.println(tests[i]);

            oos.writeObject(serverKeyPair.encrypt(tests[i].input));
            oos.flush();

            Object result = keyPair.decrypt((EncryptedObject) ois.readObject());
            if (result == null) {
                System.out.println("Actual output: null");
            }
            else {
                System.out.println("Actual output: " + (result.getClass().isArray() ? Arrays.toString((Object[]) result) : result));
            }

            System.out.println("Result: " + (tests[i].test(result) ? "\u001b[32mPASSED\u001b[0m" : "\u001b[31mFAILED\u001b[0m") + "\n\n");
            testResults[i] = tests[i].test(result);
        }

        oos.close();
        ois.close();
        lrOis.close();
        lrSock.close();
        mainSock.close();

        System.out.println("Test results----------");
        for (int i = 0; i < tests.length; i++) {
            System.out.println(tests[i].name + ": " + (testResults[i] ? "\u001b[32mPASSED\u001b[0m" : "\u001b[31mFAILED\u001b[0m"));
        }
    }

    private static JSONObject makeJSONObject(String[] keys, Object[] data) {
        JSONObject obj = new JSONObject();

        for (int i = 0; i < keys.length; i++) {
            obj.put(keys[i], data[i]);
        }

        return obj;
    }
}
