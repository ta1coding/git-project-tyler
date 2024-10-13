
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
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.time.LocalDate;

public class Git implements GitInterface {

    public void checkout(String commitHash)
    {
       //didn't do this method 
    }

    public void init() throws IOException {
        String gitDirPath = "./git";
        String objectsDirPath = gitDirPath + "/objects";
        String indexFilePath = gitDirPath + "/index";

        File gitDir = new File(gitDirPath);
        File objectsDir = new File(objectsDirPath);
        File indexFile = new File(indexFilePath);
        File headFile = new File(gitDirPath + "/HEAD");

        if (gitDir.exists() && objectsDir.exists() && indexFile.exists() && headFile.exists()) {
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
            if(!headFile.exists())
            {
                headFile.createNewFile();
            }
        }
    }

    public String commit(String author, String message) throws Exception
    {
        File commitFile = new File("./git/CommitFile");
        File headFile = new File("./git/HEAD");
        if (!commitFile.exists())
        {
            commitFile.createNewFile();
        }
        BufferedReader reader = Files.newBufferedReader(headFile.toPath());
        boolean first;
        BufferedWriter commitWriter = Files.newBufferedWriter(commitFile.toPath());
        String hash = "";
        if (!reader.ready())//would imply that there is nothing within the head file
        {
            first = true; //first root tree file created
            String treeHash =  createRootTree(first);
            commitWriter.append("tree: " + treeHash + "\n");
            commitWriter.append("parent: " + "\n"); //might want this to be append
            commitWriter.append("author: " + author + "\n");
            commitWriter.append("date: " + LocalDate.now() + "\n" );
            commitWriter.append("message: " + message + "\n");
            commitWriter.close();
            makeBlob(commitFile.getPath());
            hash = sha1Hash(commitFile);
            

        }
        else{
            first = false;
            String treeHash = createRootTree(first);
            commitWriter.write("tree: " + treeHash + "\n");
            commitWriter.write("parent: " + reader.readLine() + "\n"); //might want this to be append
            commitWriter.write("author: " + author + "\n");
            commitWriter.write("date: " + LocalDate.now() + "\n" );
            commitWriter.write("message: " + message + "\n");
            commitWriter.close();
            makeBlob(commitFile.getPath());
            hash = sha1Hash(commitFile);
            //for parent, read head file
        }
        //update headFile
        commitFile.delete();
        BufferedWriter headWriter = Files.newBufferedWriter(headFile.toPath());
        headWriter.write(hash);
        headWriter.close();
        //commitWriter.close();
        File indexFile = new File("./git/index");
        BufferedWriter indexTextDeleter = Files.newBufferedWriter(indexFile.toPath(),StandardOpenOption.TRUNCATE_EXISTING );
        indexTextDeleter.close();
        return hash;
        
        
        
    }
    public String createRootTree(boolean first) throws Exception
    {
        File indexFile = new File("./git/index");
        BufferedReader indexReader = Files.newBufferedReader(indexFile.toPath());
        File treeFile = new File ("./git/tree");
        File headFile = new File("./git/HEAD");
        if (!treeFile.exists())
        {
            treeFile.createNewFile();
        }
        BufferedWriter treeWriter = Files.newBufferedWriter(treeFile.toPath());
        String endHash = "";
        //if this is the first tree file being createdd
        
            String index = "";
            while(indexReader.ready())
            {
                index += indexReader.readLine() + "\n";
            }
            treeWriter.write(index);
        if (first)
        {
            treeWriter.close();
            makeBlob(treeFile.getPath());
            endHash = sha1Hash(treeFile);
            
        }
        else{
            //add the last parent file tree onto it
            //uncompleted
            BufferedReader reader = Files.newBufferedReader(headFile.toPath());
            String parentHash = reader.readLine();
            File parentFile = new File("./git/objects/" + parentHash);
            BufferedReader parentReader = Files.newBufferedReader(parentFile.toPath());
            String parentTree = parentReader.readLine().substring(6);
            File parentTreeFile = new File("./git/objects/" + parentTree);
            BufferedReader parentTreeReader = Files.newBufferedReader(parentTreeFile.toPath());
            String extraText = "";
            while(parentTreeReader.ready())
            {
                extraText += parentTreeReader.readLine() + "\n";
            }
            //treeWriter.write("\n");// not sure if this line is necessary or not
            treeWriter.append(extraText);
            treeWriter.close();
            makeBlob(treeFile.getPath());
            endHash = sha1Hash(treeFile);

        }
        //treeWriter.close();
        indexReader.close();
        treeFile.delete();
        //truncate all text in index file
        return endHash;
    }
    public void stage (String filePath) throws Exception
    {
        makeBlob(filePath);
        //make sure you create an index file in here if it doesn't exist because you had to delete it's contents above.
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
