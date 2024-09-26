
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

        if (!file.exists()) {
            throw new IOException("DNE: " + filename);
        }

        if (file.isFile()) {
            String sha1 = Sha1Hash(file);
            String objectsDirPath = "git/objects/";
            File newFile = new File(objectsDirPath + sha1);

            if (!newFile.exists()) {
                newFile.createNewFile();

                try (InputStream input = new FileInputStream(file); OutputStream output = new FileOutputStream(newFile)) {

                    int current;
                    while ((current = input.read()) != -1) {
                        output.write(current);
                    }
                }
            }

            String indexFilePath = "git/index";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath, true))) {
                writer.write("blob " + sha1 + " " + filename);
                writer.newLine();
            }

            System.out.println("Blob created for file: " + filename);
        } else if (file.isDirectory()) {
            File[] filesAndDirs = file.listFiles();
            if (filesAndDirs == null) {
                throw new IOException("Nun inside: " + filename);
            }

            StringBuilder treeContent = new StringBuilder();
            for (File child : filesAndDirs) {
                makeBlob(child.getPath());

                String sha1 = Sha1Hash(child);
                if (child.isFile()) {
                    treeContent.append("blob ").append(sha1).append(" ").append(child.getName()).append("\n");
                } else if (child.isDirectory()) {
                    treeContent.append("tree ").append(sha1).append(" ").append(child.getName()).append("\n");
                }
            }

            byte[] contentBytes = treeContent.toString().getBytes();
            String treeSha1 = Sha1Hash(contentBytes);
            String objectsDirPath = "git/objects/";
            File treeFile = new File(objectsDirPath + treeSha1);

            if (!treeFile.exists()) {
                try (OutputStream output = new FileOutputStream(treeFile)) {
                    output.write(contentBytes);
                }
            }

            String indexFilePath = "git/index";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath, true))) {
                writer.write("tree " + treeSha1 + " " + filename);
                writer.newLine();
            }

            System.out.println("Tree created for directory: " + filename);
        }
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

    public String Sha1Hash(byte[] contentBytes) {
        BigInteger sha1data = new BigInteger(1, contentBytes);
        String hash = sha1data.toString(16);
        while (hash.length() < 40) {
            hash = "0" + hash;
        }
        return hash;
    }
}
