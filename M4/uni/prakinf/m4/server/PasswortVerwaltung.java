package uni.prakinf.m4.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class PasswortVerwaltung {
    private static final String FILENAME = ".cpasswords";
    private static Map<String, String> logins;

    public static boolean passwortGueltig(String name, String password) {
        boolean login_valid = false;
        loadLogins();

        if (logins.containsKey(name)) {
            login_valid = logins.get(name).equals(password);
        } else {
            logins.put(name, password);
            login_valid = true;
        }

        saveLogins();

        return login_valid;
    }

    @SuppressWarnings("unchecked")
    private static void loadLogins() {
        try {
            FileInputStream fis = new FileInputStream(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);

            logins = (HashMap<String, String>) ois.readObject();
        } catch (Exception ex) {
            logins = new HashMap<String, String>();
        }
    }

    private static void saveLogins() {
        try {
            FileOutputStream fos = new FileOutputStream(FILENAME, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(logins);
        } catch (Exception ex) {

        }
    }
}
