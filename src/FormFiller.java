import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class FormFiller {
    private static List<String> pdfFields;
    private String last;
    private String first;
    private String middle;
    private String aNum;
    private File inputFile;

    // Constructor that copies inputFile and expands info
    FormFiller(File inputFile, List<String> info){
        this.inputFile=inputFile;
        this.last=info.get(0);
        this.first=info.get(1);
        this.middle=info.get(2);
        this.aNum=info.get(3);
    }

    // Fills fields and saves as new pdf
    void fill(){
        pdfFields = new ArrayList<>();

        try {
            PDDocument pdfDocument = PDDocument.load(inputFile);
            PDDocumentCatalog docCatalog = pdfDocument.getDocumentCatalog();
            PDAcroForm acroForm = docCatalog.getAcroForm();
            acroForm.setNeedAppearances(true);

            // Recursively expands all fields in the acro form then adds it to pdfFields
            List<PDField> l = acroForm.getFields();
            for(PDField f : l){
                addToList(f);
            }

            // Loops through all fields and fills target fields
            for(String fullyName : pdfFields){
                PDField field = acroForm.getField(fullyName);

                if(!fullyName.contains("subform[0]")) {
                    if (fullyName.contains("Pt1Line1b_GivenName")) {
                        field.setValue(first);
                    } else if (fullyName.contains("Pt1Line1a_FamilyName")) {
                        field.setValue(last);
                    } else if (fullyName.contains("Pt1Line1c_MiddleName")) {
                        field.setValue(middle);
                    } else if (fullyName.contains("Pt1Line3e_AlienNumber")) {
                        field.setValue(aNum);
                    }
                }
            }

            // Save new pdf
            pdfDocument.setAllSecurityToBeRemoved(true);
            pdfDocument.save(getOutputFile(inputFile));
            pdfDocument.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    // Gets the output file
    private File getOutputFile(File inputFile) {
        String sanitizedLast = "_" + last.replaceAll(" ", "_").replaceAll("/", "");

        String fullPath = inputFile.getAbsolutePath();
        String oFilename = FilenameUtils.removeExtension(fullPath) + sanitizedLast + "." + FilenameUtils.getExtension(fullPath);
        File outputFile = new File(oFilename);

        // Renames file to inputFile_commented_n and keeps increasing the number n until the file does not exist
        int n = 1;
        while (outputFile.exists()) {
            oFilename = FilenameUtils.removeExtension(fullPath) + sanitizedLast + "_" + n + "." + FilenameUtils.getExtension(fullPath);
            outputFile = new File(oFilename);
            n++;
        }

        return outputFile;
    }

    // Recursively expands fields
    private static void addToList(PDField field)
    {
        pdfFields.add(field.getFullyQualifiedName());
        if (field instanceof PDNonTerminalField)
        {
            PDNonTerminalField nonTerminalField = (PDNonTerminalField) field;
            for (PDField child : nonTerminalField.getChildren())
            {
                addToList(child);
            }
        }
    }
}
