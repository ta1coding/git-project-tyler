
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GitTester {

    public static void main(String[] args) {
        try {
            Git git = new Git();

            System.out.println("Initializing Git repository...");
            git.init();

            createTestDirectoryStructure();

            System.out.println("\n--- Adding Individual Files ---");
            git.makeBlob("file1.txt");
            git.makeBlob("file2.txt");

            System.out.println("\n--- Adding Directory 'my_project' with Subdirectories and Files ---");
            git.makeBlob("my_project");

            System.out.println("\n--- Adding Directory 'other_project' with Subdirectories and Files ---");
            git.makeBlob("other_project");

            System.out.println("\n--- Git Repository Testing Completed Successfully ---");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTestDirectoryStructure() throws IOException {
        createTestFile("file1.txt", "This is the content of file 1.");
        createTestFile("file2.txt", "This is the content of file 1.");

        File myProject = new File("my_project");
        if (!myProject.exists()) {
            myProject.mkdir();
        }

        File subDir1 = new File("my_project/subdir1");
        if (!subDir1.exists()) {
            subDir1.mkdir();
        }

        File subDir2 = new File("my_project/subdir2");
        if (!subDir2.exists()) {
            subDir2.mkdir();
        }

        createTestFile("my_project/file3.txt", "This is file 3 in my_project.");
        createTestFile("my_project/subdir1/file4.txt", "This is file 4 in subdir1.");
        createTestFile("my_project/subdir1/file5.txt", "This is file 5 in subdir1.");
        createTestFile("my_project/subdir2/file6.txt", "This is file 6 in subdir2.");

        File otherProject = new File("other_project");
        if (!otherProject.exists()) {
            otherProject.mkdir();
        }

        File otherSubDir1 = new File("other_project/subdir1");
        if (!otherSubDir1.exists()) {
            otherSubDir1.mkdir();
        }

        File otherSubDir2 = new File("other_project/subdir2");
        if (!otherSubDir2.exists()) {
            otherSubDir2.mkdir();
        }

        createTestFile("other_project/file7.txt", "This is file 7 in other_project.");
        createTestFile("other_project/subdir1/file8.txt", "This is file 8 in subdir1 of other_project.");
        createTestFile("other_project/subdir1/file9.txt", "This is file 9 in subdir1 of other_project.");
        createTestFile("other_project/subdir2/file10.txt", "This is file 10 in subdir2 of other_project.");

        System.out.println("Test directory structure created successfully.");
    }

    private static void createTestFile(String fileName, String content) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
            System.out.println("Test file created: " + fileName);
        }
    }
}
