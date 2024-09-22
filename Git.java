
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git {

    public static void main(String[] args) {

    }

    public void init() throws IOException {
        String gitDirPath = "git";
        String objectsDirPath = gitDirPath + "/objects";
        String indexFilePath = gitDirPath + "/index";

        File gitDir = new File(gitDirPath);
        File objectsDir = new File(objectsDirPath);
        File indexFile = new File(indexFilePath);

        if (gitDir.exists()) {
            System.out.println("Git Repository already exists");
        } else {
            gitDir.mkdir();

            if (objectsDir.exists()) {
                System.out.println("Objects directory already exists");
            } else {
                objectsDir.mkdir();
                System.out.println("Created Objects directory");
            }

            if (indexFile.exists()) {
                System.out.println("Index file already exists");
            } else {
                indexFile.createNewFile();
                System.out.println("Created Index file");
            }
        }
    }

    public void makeBlob(String filename) throws Exception {
        File file = new File(filename);
        String sha1 = sha1Code(filename);
        String sha1Name = sha1 + ".file";
        String objectsDirPath = "git/objects/";
        File newFile = new File(objectsDirPath + sha1Name);

        newFile.createNewFile();

        InputStream in;
        OutputStream out;
        in = new FileInputStream(file);
        out = new FileOutputStream(newFile);

        int current;
        while ((current = in.read()) != -1) {
            out.write(current);
        }

        in.close(); 
        out.close();

        String indexFilePath = "git/index";
        BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath));
        writer.write(sha1 + " " + filename);
        writer.newLine();

    }
    
    // https://gist.github.com/zeroleaf/6809843
    public String sha1Code(String filePath) throws IOException, NoSuchAlgorithmException {
        try (FileInputStream fileInputStream = new FileInputStream(filePath); DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, MessageDigest.getInstance("SHA-1"))) {

            byte[] bytes = new byte[1024];
            // Read all file content
            while (digestInputStream.read(bytes) > 0) {
                // Reading file content, digest is updated automatically
            }

            // Get the digest from the DigestInputStream
            byte[] resultByteArray = digestInputStream.getMessageDigest().digest();
            return bytesToHexString(resultByteArray);
        }
    }

    // https://gist.github.com/zeroleaf/6809843
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(value).toUpperCase());
        }
        return sb.toString();
    }
}
