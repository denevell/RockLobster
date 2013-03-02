package utils;

import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class FileUtils {

	public static void createDir(String dir) {
		File f = new File(dir);
		f.mkdir();
	}
	
	public static String getStringFromFile (String fileLocation) {
		Scanner in = null;
		try {		
			File f = new File(fileLocation);
			if(f.exists()) {
				in = new Scanner(new FileReader(fileLocation));
				String s, str="";
				while(in.hasNext() && (s=in.nextLine())!=null) str+=s+"\n";
				return str;
			} else {
				return null; 
			}					
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if(in!=null) in.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}	

}
