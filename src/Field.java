import javax.swing.*;

// Helper class for Main to prompt for different pieces of information
class Field {

    private String name;
    private String output;

    Field (String name){
        this.name = name;
    }

    void prompt() {
        output = JOptionPane.showInputDialog(null, "Enter " + name, Main.title, JOptionPane.PLAIN_MESSAGE);
        if (output == null) {
            System.exit(0);
        }
    }

    String getOutput(){
        return output;
    }
}
