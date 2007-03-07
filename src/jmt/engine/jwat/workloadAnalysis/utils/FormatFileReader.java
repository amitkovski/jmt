package jmt.engine.jwat.workloadAnalysis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FormatFileReader {
	private int numeroVariabili;
	private ArrayList names;
	private ArrayList comments;
	private ArrayList delimiters;
	private ArrayList regExpr;
	private ArrayList defaults;
	private ArrayList replaces;
	private boolean[] selected;
	private int[] types;
	private File fileName;
	private int current = -1;
	
	public FormatFileReader(String fileN){
		fileName = new File(fileN);
		// Caricamento dati da file formato
		try{
			BufferedReader file = new BufferedReader(new FileReader(fileName));
			// Retrive number of variables
			numeroVariabili = Integer.parseInt(file.readLine());
			types = new int[numeroVariabili];
			selected = new boolean[numeroVariabili];
			names = new ArrayList();
			comments = new ArrayList();
			delimiters = new ArrayList();
			regExpr = new ArrayList();
			defaults = new ArrayList();
			replaces = new ArrayList();
			// Retrive single variable information
			for(int i = 0;i < numeroVariabili;i++){
				names.add(file.readLine());
				types[i] = Integer.parseInt(file.readLine());
				selected[i] = (Integer.parseInt(file.readLine()) == 0 ? false : true);
				comments.add(file.readLine());
				delimiters.add(file.readLine());
				regExpr.add(file.readLine());
				defaults.add(file.readLine());
				replaces.add(file.readLine());
			}
			current = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e1){
			e1.printStackTrace();
		}
	}
	public String getName(){
		if(current != -1) return (String) names.get(current);
		else return null;
	}
	public int getType(){
		if(current != -1) return types[current];
		else return -1;
	}
	public boolean getSelect(){
		if(current != -1) return selected[current];
		else return false;
	}
	public String getComment(){
		if(current != -1) return (String) comments.get(current);
		else return null;
	}
	public String getDelimiters(){
		if(current != -1) return (String) delimiters.get(current);
		else return null;
	}
	public String getRegExpr(){
		if(current != -1) return (String) regExpr.get(current);
		else return null;
	}
	public String getDefaults(){
		if(current != -1) return (String) defaults.get(current);
		else return null;
	}
	public String getReplace(){
		if(current != -1) return (String) replaces.get(current);
		else return null;
	}
	public boolean next(){
		if(current < names.size()-1){
			current++;
			return true;
		}
		return false;
	}
	public int getNumVars(){
		if(current != -1) return names.size();
		return 0;
	}
}

