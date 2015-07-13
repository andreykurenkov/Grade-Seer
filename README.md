A simple Java open source grading framework with a command line or GUI interface that assists with batch autograding, manual grading, or plagiarism detection. 

The batch autograding and plagiarism detection is the latest development, and is a pretty simple but useful way to launch a grading program to iterate over student directories and grade submissions or check them for plagiarism. The example package should make it clear how the code can be used - due to using Interfaces and Abstract classes, it is quite flexible. 

The GUI was written prior to the batch autograding and plagiarism detection functionality. It is rather large - it can parse specific local directory structure to create a visual list of students, from there a student can be selected and the files of their submission viewed in a separate list, and once a file is chosen it can be read and also ran (currently, this only works with Java programs). The GUI also uses the list of students to have a GUI with fields for grades and comments for each one, which can all be saved and loaded locally. I used it extensively while grading over developing scrips or using cd, but it may take some work to understand and adapt to a different system.

Please let me know if you use this for you work (my current assumption is that no one ever will).
