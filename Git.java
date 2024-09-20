
import java.io.File;

public class Git {

    public static void main(String[] args) {

    }

    public void init() {

        String gitDirPath = "git";
        String objectsDirPath = gitDirPath + "/objects";
        String indexFilePath = gitDirPath + "/index";

        File gitDir = new File(gitDirPath);
        File objects = new File(objectsDirPath);
        File index = new File(indexFilePath);
        if (gitDir.exists()) {
            System.out.println("Git Repository already exists");
        } else {
            gitDir.mkdir();
            if (objects.exists()) {
                System.out.println("Objects file already exists");
            } else {
                gitDir.mkdir();
            }
            if (index.exists()) {
                System.out.println("Index file already exists");
            } else {
                gitDir.mkdir();
            }
        }
    }
}
