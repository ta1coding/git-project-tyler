# git-project-tyler
1. Did you code stage()/ how well does it work?
    My stage() function works as intended. When you add in a file path to the stage function, it recursively adds those objects as blobs into the object folder, and saves them in the index file. This primes the code for the commit method. There are no flaws in functionality within this method
2. Did you code commit()/ how well does it work?
    My commit() function has no flaws, and creates a tree file that uses the text within the index file and the parent tree file's text (if there is a parent commit). It then creates a commit blob using this tree, the parent commit file, and other inputs, and rewrites the text in HEAD to point to the new commit blob. The function then deletes all text within the index file.
3. Did you do checkout / how well does it work?
    I did not do this method.
4. What bugs did find / which of em did you fix?
    I found various bugs in my code, all of which have been fixed. In this implementation, the only bug I came across was not closing my bufferedWriter before creating the blob. My code worked fine otherwise. 

How to use the GitInterface:
First, stage any files/directories that you wish by calling the stage(String filePath) function. This will create a blob of the file, and recursively create blobs of all subfiles if it is a directory, and save this file in the objects folder and the index file. Stage as many objects as you would like, calling separate stage functions on each one.

Then, use the commit(String author, String message) function to add a commit blob. This will implement the functionality listed in bullet point 2. After that, you can stage and commit more files and directories. 