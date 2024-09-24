
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git {

    public void init() throws IOException {
        String gitDirPath = "./git";
        String objectsDirPath = gitDirPath + "/objects";
        String indexFilePath = gitDirPath + "/index";

        File gitDir = new File(gitDirPath);
        File objectsDir = new File(objectsDirPath);
        File indexFile = new File(indexFilePath);

        if (gitDir.exists() && objectsDir.exists() && indexFile.exists()) {
            System.out.println("Git Repository already exists");
        } else {
            if (!gitDir.exists()) {
                gitDir.mkdir();
            }

            if (!objectsDir.exists()) {
                objectsDir.mkdir();
            }

            if (!indexFile.exists()) {
                indexFile.createNewFile();
            }
        }
    }

    public void makeBlob(String filename) throws Exception {
        File file = new File(filename);
        String sha1 = Sha1Hash(file);
        String objectsDirPath = "git/objects/";
        File newFile = new File(objectsDirPath + sha1);

        if (newFile.exists()) {
            System.out.println("The file has already been turned into a blob.");
            return;
        }

        newFile.createNewFile();

        InputStream input = new FileInputStream(file);
        OutputStream output = new FileOutputStream(newFile);

        int current;
        while ((current = input.read()) != -1) {
            output.write(current);
        }

        input.close();
        output.close();

        String indexFilePath = "git/index";

        if (isAlreadyInIndex(indexFilePath, sha1)) {
            System.out.println("The file is already in index");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath, true))) {
            writer.write(sha1 + " " + filename);
            writer.newLine();
        }
    }

    private boolean isAlreadyInIndex(String indexFilePath, String sha1) throws IOException {
        File indexFile = new File(indexFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(indexFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(sha1)) {
                    return true;
                }
            }
        }

        return false;
    }

    // https://www.geeksforgeeks.org/sha-1-hash-in-java/
    public String Sha1Hash(File file) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            byte[] sha1bytes = digester.digest(Files.readAllBytes(file.toPath()));
            BigInteger sha1data = new BigInteger(1, sha1bytes);
            String hash = sha1data.toString(16);
            while (hash.length() < 40) {
                hash = "0" + hash;
            }
            return hash;
        } catch (IOException | NoSuchAlgorithmException e) {
        }
        return null;
    }
}
