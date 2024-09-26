
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

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

    private static boolean repoExists() {
        String gitDirPath = "./git";
        String objectsDirPath = gitDirPath + "/objects";
        String indexFilePath = gitDirPath + "/index";

        File gitDir = new File(gitDirPath);
        File objectsDir = new File(objectsDirPath);
        File indexFile = new File(indexFilePath);

        if (gitDir.exists() && objectsDir.exists() && indexFile.exists()) {
            return true;
        }
        return false;
    }

    public void makeBlob(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(file.getName() + " does not exist.");
        }

        // ensure that the repo exists before proceeding.
        if (!repoExists())
            init();
        // check if the file is a didectory and runs recursive backup
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                String path = f.getPath();
                makeBlob(path);
            }
        }

        // get the hash of the file
        String hash = "";
        String objectsDirPath = "git/objects/";
        File newFile;
        String index;
        index = getIndex();
        final String EMPTY_FILE_HASH = "da39a3ee5e6b4b0d3255bfef95601890afd80709";

        //ensures that the file can be read before hashing
        if (file.canRead()) {
            hash = sha1Hash(file);
            newFile = new File(objectsDirPath + hash);
        }
        else {
            hash = EMPTY_FILE_HASH;
            newFile = new File(objectsDirPath + hash);
        }

        // check that the blob & hash pair hasn't already been created
        if (!newFile.exists()) {
            // simple copy if file is a file
            if (file.isFile()) {
                if (!file.canRead()) {
                    newFile.createNewFile();
                } else {
                    Files.copy(Path.of(file.getPath()), Path.of(newFile.getPath()));
                }
            }
            // creates a tree file with appropriate data if file is a directory
            else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
                for (File f : file.listFiles()) {
                    // finds the hash
                    BufferedReader reader = new BufferedReader(new FileReader("git/index"));
                    String fHash = "";
                    while (reader.ready()){
                        String line = reader.readLine();
                        String fileAtLine = line.substring(line.length() - f.getPath().length());
                        if (Objects.equals(fileAtLine, f.getPath())) {
                            fHash = line.substring(5, 45);
                            break;
                        }
                    }
                    reader.close();
                    // adds the hash to the tree
                    if (f.isFile()) {
                        writer.write("blob " + fHash + " " + f.getName());
                    } else {
                        writer.write("tree " + fHash + " " + f.getName());
                    }
                    writer.newLine();
                }
                writer.close();
            }
        }

        String indexFilePath = "git/index";

        if (index.contains(hash + " " + file.getPath())) {
            System.out.println("The file is already in index");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(indexFilePath, true))) {
            if (file.isFile()) {
                writer.write("blob" + " " + hash + " " + filePath);
            } else {
                writer.write("tree" + " " + hash + " " + filePath);
            }

            writer.newLine();
            writer.close();
        }
    }

    private static String getIndex() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader("git/index"));
        while (reader.ready()) {
            stringBuilder.append(reader.readLine());
        }
        reader.close();
        return stringBuilder.toString();
    }

    // https://www.geeksforgeeks.org/sha-1-hash-in-java/
    public String sha1Hash(File file) throws NoSuchAlgorithmException, IOException {
        if (file.isFile()) {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] fileData = new byte[(int) file.length()];
            fileInputStream.read(fileData);
            fileInputStream.close();
            byte[] sha1bytes = digester.digest(fileData);
            BigInteger sha1data = new BigInteger(1, sha1bytes);
            String hash = sha1data.toString(16);
            while (hash.length() < 40) {
                hash = "0" + hash;
            }
            return hash;
        } else {
            String directoryData = getDirectoryData(file.getPath());
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            byte[] fileData = directoryData.getBytes();
            byte[] sha1bytes = digester.digest(fileData);
            BigInteger sha1data = new BigInteger(1, sha1bytes);
            String hash = sha1data.toString(16);
            while (hash.length() < 40) {
                hash = "0" + hash;
            }
            return hash;
        }
    }

    /**
     * @param filePath - the path to the directory
     * @return The file and folder contents of the directory in String form
     * @throws FileNotFoundException
     */
    private static String getDirectoryData(String filePath) throws FileNotFoundException {
        File directory = new File(filePath);
        if (!directory.exists() && directory.isDirectory()) {
            throw new FileNotFoundException(filePath + " is not a directory.");
        }
        StringBuilder string = new StringBuilder();
        string.append("../");
        string.append("\n");
        string.append("./");
        string.append("\n");

        for (File file : directory.listFiles()) {
            string.append(file.getName());
            string.append("\n");
        }

        return string.toString();
    }
}
