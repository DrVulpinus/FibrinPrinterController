package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProfileManager{
	Properties props = new Properties();
	FileInputStream fis;
	FileOutputStream fos;
	File currentProfile;
	public ProfileManager(){
		
	}
	
	/**
	 * Loads a profile file from the given path
	 * @param path The path to load the profile from
	 */
	public void loadProfile(File _file){
		currentProfile = _file;
		try {
			fis = new FileInputStream(currentProfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			props.loadFromXML(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Loads a profile without a predetermined path from a FileChooser
	 */
	public void loadProfile(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("XML Profiles", "xml");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION){
			loadProfile(fc.getSelectedFile());
		}
	}
	public void saveProfile(File _file){
		File newFile = _file;
		if(!newFile.getPath().endsWith(".xml")){
			String newPath = newFile.getAbsolutePath();
			newPath += ".xml";
			newFile = new File(newPath);
		}
		try {
			fos = new FileOutputStream(newFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		try {
			props.storeToXML(fos, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void saveProfile(){
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter filter = new FileNameExtensionFilter("XML Profiles", "xml");
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(filter);		
		int result = fc.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION){			
			saveProfile(fc.getSelectedFile());			
		}
	}
	
	public void setProfileOption(Object _key, Object _value){
		System.out.println(_value.getClass());
		String out = _value.getClass().getName();
		out += ":::";
		out += _value.toString();
		props.put(_key, out);
	}
	public Object getProfileOption(String _key){
		
		String raw = props.getProperty(_key);
		String[] raws = raw.split(":::");
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();	
			if (cl.loadClass(raws[0]) == Boolean.class){
				return Boolean.parseBoolean(raws[1]);
			}
			else if (cl.loadClass(raws[0]) == Integer.class){
				return Integer.parseInt(raws[1]);
			}
			else if (cl.loadClass(raws[0]) == String.class){
				return raws[1];
			}
			else if (cl.loadClass(raws[0]) == Float.class){
				System.out.println("Number");
			}
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		
		
	}

}
