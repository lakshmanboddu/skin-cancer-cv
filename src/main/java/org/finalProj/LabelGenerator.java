package org.finalProj;
/*
@Author : Lakshman Boddu, Kamalpreet Kaur
This new class is created as supplement to the already existing
datavec classes to conveniently read the metadata csv file that
accompanies the HAM10000 dataset so as to read the labels

using pre-existing interface from the package org.datavec.api.io.labels
The package only has two classes:
 - ParentPathLabelGenerator -> Return parent folder name
 - PatternPathLabelGenerator -> return the file name
We need something else, so we created this class
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.labels.PathLabelGenerator;
import org.datavec.api.writable.Text;
import org.datavec.api.writable.Writable;

public class LabelGenerator implements PathLabelGenerator {
    private HashMap<String,String> map = new HashMap<>();

    public LabelGenerator() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("ham10000/HAM10000_metadata.csv"));

        String line = br.readLine();
        while((line=br.readLine())!=null){
            String str[] = line.split(",");
            for(int i=1;i<str.length;i++){
                this.map.put(str[1], str[2]);
            }
        }

    }

    public Writable getLabelForPath(String path) {
        String fileName = FilenameUtils.getName((new File(path)).getName());
        //Returns label for a give filename
        return new Text(this.map.get(fileName));
    }

    public Writable getLabelForPath(URI uri) {
        return this.getLabelForPath((new File(uri)).toString());
    }

    public boolean inferLabelClasses() {
        return true;
    }
}
