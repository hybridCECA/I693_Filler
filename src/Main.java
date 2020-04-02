import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final String title = "Chen's I693 Filler";
    private static final String[] fieldNames = {"Last Name", "First Name", "Middle Name", "A-Number"};

    public static void main(String[] args) {
        File inputFile = getFile();

        int promptAgain;
        // For each patient, prompts for information then fills form
        do {
            List<String> info = new ArrayList<>();
            for (String name : fieldNames) {
                Field f = new Field(name);
                f.prompt();
                info.add(f.getOutput());
            }
            FormFiller f = new FormFiller(inputFile, info);
            f.fill();

            promptAgain = JOptionPane.showConfirmDialog(null, "Would you like to enter another patient?", title, JOptionPane.YES_NO_OPTION);
        } while (promptAgain == JOptionPane.YES_OPTION);
    }

    // Obtains input PDF file. Defualt is I693.pdf in the working directory but a JFileChooser is used if it is not found
    private static File getFile() {
        File defaultFile = new File("I693.pdf");
        if(defaultFile.exists()){
            return defaultFile;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Open PDF File");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            System.err.println("No input file selected");
            System.exit(1);

            return null;
        }
    }
}
