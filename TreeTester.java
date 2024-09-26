import java.io.File;
import java.io.IOException;

public class TreeTester {
    public static void main(String[] args) throws Exception {
        removeDirectory("test");
        removeDirectory("git");
        makeTestFiles();

        Git git = new Git();
        git.makeBlob("test");

        verifyResults();
    }

    public static void makeTestFiles () throws IOException {
        File test = new File("test");
        File test2 = new File("test/test");
        File test3 = new File("test/test2");
        File test4 = new File("test/test2/test3");
        File test5 = new File("test/test2/test3/test5");
        File test6 = new File("test/test2/test4");

        test.mkdir();
        test2.createNewFile();
        test3.mkdir();
        test4.mkdir();
        test5.createNewFile();
        test6.createNewFile();
    }

    /**
     * Deletes all .txt files from the working directory. Removes git/objects
     * directory and all of its contents
     */
    public static void cleanWorkspace() {
        File directory = new File("./");
        for (File file : directory.listFiles()) {
            if (file.getName().contains(".txt"))
                file.delete();
        }
        // removes the objects folder and its contents
        removeDirectory("git/objects");
    }

    /**
     * Deletes a directory and all files within it
     * 
     * @param directoryName - the directory to delete
     */
    private static void removeDirectory(String directoryName) {
        File directory = new File(directoryName);
        if (!directory.exists())
            return;
        for (File file : directory.listFiles()) {
            if (file.isDirectory())
                removeDirectory(file.getPath());
            file.delete();
        }
        directory.delete();
    }

    /**
     * @param length - the length of the string
     * @return a string of random lower case letters
     */
    private static String randomString(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append(randomChar());
        }
        return name.toString();
    }

    /**
     * @return a random lower case character
     */
    private static char randomChar() {
        return (char) (int) (Math.random() * (122 - 97) + 97);
    }

    /**
     * @param low  - the low value
     * @param high - the high value
     * @return a random int between low and high: inclusive, exclusive
     */
    private static int randomInt(int low, int high) {
        return (int) (Math.random() * (high - low) + low);
    }

    private static void verifyResults () {
        System.out.println("Method worked successfully.");
    }
}
