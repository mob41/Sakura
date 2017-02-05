package com.github.mob41.sakura.image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.JSONObject;

public class ImageManager {
	
	private static final String DATA_FILE_NAME = "images_assignments.json";

	private JSONObject images;
	
	public ImageManager() {
		loadFile();
	}
	
	public boolean addImage(String imageName, Image image){
		images.put(imageName, image2json(image));
		writeFile();
		return true;
	}
	
	public boolean removeImage(String imageName){
		if (images.isNull(imageName)){
			return false;
		}
		images.remove(imageName);
		writeFile();
		return true;
	}
	
	public Image getImage(String imageName){
		if (images.isNull(imageName)){
			return null;
		}
		return json2image(images.getJSONObject(imageName));
	}
	
	public static JSONObject image2json(Image image){
		JSONObject out = new JSONObject();
		
		if (image.getImageType() == Image.IMAGE_FONT_AWESOME_ICON){
			out.put("type", image.getImageType());
			out.put("fa_str", image.getFontAwesomeString());
			out.put("fa_size", image.getFontAwesomeSize());
		} else if (image.getImageType() == Image.IMAGE_URL_IMAGE) {
			out.put("type", image.getImageType());
			out.put("uri", image.getURI());
		} else {
			out.put("type", "unknown");
		}
		
		return out;
	}
	
	public static Image json2image(JSONObject json){
		if (json.getInt("type") == Image.IMAGE_FONT_AWESOME_ICON){
			return new Image(json.getString("fa_str"), json.getString("fa_size"));
		} else if (json.getInt("type") == Image.IMAGE_URL_IMAGE){
			return new Image(json.getString("uri"));
		} else {
			return null;
		}
	}
	
	public void loadFile(){
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists()){
				writeFile();
				return;
			}
			FileInputStream out = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(out));
			String line;
			String data = "";
			while ((line = reader.readLine()) != null){
				data += line;
			}
			reader.close();
			
			images = new JSONObject(data);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void writeFile(){
		try {
			File file = new File(DATA_FILE_NAME);
			if (!file.exists() || images == null){
				images = new JSONObject();
			}
			FileOutputStream out = new FileOutputStream(file);
			PrintWriter writer = new PrintWriter(out, true);
			writer.println(images.toString(5));
			writer.close();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

}
