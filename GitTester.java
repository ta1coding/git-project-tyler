
import java.io.File;

public class GitTester {

    public static void main(String[] args) {
        try {
            Git git = new Git();

            System.out.println("Initializing Git repository...");
            git.init();

            System.out.println("Adding file1.txt to the repository...");
            git.makeBlob("file1.txt");

            System.out.println("Adding file2.txt to the repository...");
            git.makeBlob("file2.txt"); 

            System.out.println("Adding a directory (components) to the repository...");
            File dir = new File("components");
            if (!dir.exists()) {
                dir.mkdir();
            }
            git.makeBlob("components");

            System.out.println("Adding file3.txt to the repository...");
            git.makeBlob("file3.txt");

            System.out.println("Adding a directory (assets) to the repository...");
            File dir2 = new File("assets");
            if (!dir2.exists()) {
                dir2.mkdir();
            }
            git.makeBlob("assets");

            System.out.println("Repository initialized and blobs/trees created successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
