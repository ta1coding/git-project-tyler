public interface GitInterface {

    /**
     * Stages a file for the next commit.
     *
     * @param filePath The path to the file to be staged.
     * @throws Exception
     */
    void stage(String filePath) throws Exception;

    /**
     * Creates a commit with the given author and message.
     * It should capture the current state of the repository,
     * update the HEAD, and return the commit hash.
     *
     * @param author  The name of the author making the commit.
     * @param message The commit message describing the changes.
     * @return The SHA1 hash of the new commit.
     * @throws Exception
     */
    String commit(String author, String message) throws Exception;

    /**
     * EXTRA CREDIT: Checks out a specific commit given its hash.
     * This should update the working directory to match the
     * state of the repository at that commit.
     *
     * @param commitHash The SHA1 hash of the commit to check out.
     */
    void checkout(String commitHash);
}
