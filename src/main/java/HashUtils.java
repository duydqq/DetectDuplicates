import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class HashUtils {

    static String fileToHash(File file, String algorithm, int bufferSize) throws IOException {
        try {
        MessageDigest md = null;

            md = MessageDigest.getInstance(algorithm);


        InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[bufferSize];
            int nread;

            while ((nread = is.read(buffer)) > 0) {
                md.update(buffer, 0, nread);
            }

            byte[] bytes = md.digest();
            is.close();
            StringBuilder sb = new StringBuilder();

            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xFF) + 256, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: The algorithm '"+algorithm+"' does not exist.");
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
