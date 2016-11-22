package edu.ucdavis.gc.bm.descriptorGroup;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public  class Group {
	/**
	 * name of the group in D2K format
	 */
	private String nameD2K;
	/**
	 * name of the group
	 */
	private String name;
	/**
	 * list of descriptors in group; list of objects of class Descriptor
	 */
	private List<Descriptor> descriptors;
	
	public Group(){
		
	}
	// set get methods
	public  void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public  void setNameD2K(String name){
		this.nameD2K = name;
	}
	
	public String getNameD2K(){
		return this.nameD2K;
	}
	
	public void setDescriptors(List<Descriptor> descriptors){
		this.descriptors = descriptors;
	}
	
	public List<Descriptor> getDescriptors(){
		return this.descriptors;
	}
	
	public int getNumberMembers(){
		return this.descriptors.size();
	}
	/**
	 * get number of segments from the first descriptor in the group
	 * @return
	 */
	public int getNumberSegments(){
		return this.descriptors.get(0).getNumberSegments(); 
	}
	/**
	 * 
	 * @return the root descriptor of the group;
	 * in case if the root descriptor isn't present in the group return the first descriptor with complete sequence
	 */
	public Descriptor getRootDescriptor(){
		Descriptor result = descriptors.get(0);
		boolean hasGapsResult = hasGaps(result);
		for(Descriptor descriptor : descriptors){
			if(this.name.startsWith(descriptor.getName())){
				result = descriptor;break;
			}
			if(hasGapsResult){
				if(!hasGaps(descriptor)){
					result = descriptor;
					hasGapsResult = false;
				}				
			}
		}
		return result;
	}
	/**
	 * 
	 * @param descriptorName
	 * @return the descriptor by it's name
	 */
	public Descriptor getDescriptorByName(String descriptorName){
		Descriptor result = null;
		for(Descriptor descriptor : descriptors){
			if(descriptorName.equalsIgnoreCase(descriptor.getName())){
				result = descriptor;
				break;
			}
		}
		return result;
	}
	/**
	 * check if the descriptor has complete segments - without gaps indicating as '.';
	 * this method is utilized when the root descriptor isn't in the group (it was thrown off the group for some reasons, for example due to the corresponding protein was in test set)
	 * in such cases we select the representative of the group with full sequences in all segments
	 * @param descriptor
	 * @return TRUE if any segment of the descriptor has the gap in sequence
	 */
	private boolean hasGaps(Descriptor descriptor){
		for(String segmentSeq : descriptor.getSeqs()){
			if(segmentSeq.matches(".*\\..*")){
				return true;
			}
		}
		return false;
	}
	
	public String toString(){
		StringBuilder result = new StringBuilder("GROUP:  ");
		result.append(this.getName() + ":  " + this.getNumberMembers() + "\n");
		for(Descriptor desc : this.getDescriptors()){
			result.append(desc.toString() + "\n");
		}
		return result.toString();
	}
	
	public String getSummaryStat(){
		StringBuilder result = new StringBuilder("");
		TreeSet<String> folds = new TreeSet<String>();
		TreeSet<String> superFamilies = new TreeSet<String>();
		TreeSet<String> families = new TreeSet<String>();
		
		for (Descriptor desc : descriptors) {
			String astralClass = desc.getFoldAstral();
			String fold = astralClass.substring(0, astralClass.indexOf('.', 2));
			folds.add(fold);
			String superFamily = astralClass.substring(0,
					astralClass.indexOf('.', fold.length() + 1));
			superFamilies.add(superFamily);
			String family = astralClass;
			families.add(family);
		}
		result.append("Fld: ");  
		result.append(folds.size());
		result.append(", Sf: ");
		result.append(superFamilies.size());
		result.append(",  Fml: "); 
		result.append(families.size());
		result.append(", Dscr:");
		result.append(descriptors.size());
		return result.toString();
	}
	
	public int getNumberFolds(){
		TreeSet<String> folds = new TreeSet<String>();
		for (Descriptor desc : descriptors) {
			String astralClass = desc.getFoldAstral();
			String fold = astralClass.substring(0, astralClass.indexOf('.', 2));
			folds.add(fold);
		}
		return folds.size();
	}
	
	public int getNumberSuperFamilies(){
		TreeSet<String> superFamilies = new TreeSet<String>();
		for (Descriptor desc : descriptors) {
			String astralClass = desc.getFoldAstral();
			String superFamily = astralClass.substring(0,
					astralClass.lastIndexOf('.'));
			superFamilies.add(superFamily);
		}
		return superFamilies.size();
	}
	
	public int getNumberFamilies(){
		TreeSet<String> families = new TreeSet<String>();
		for (Descriptor desc : descriptors) {
			families.add(desc.getFoldAstral());
		}
		return families.size();
	}
	
	public HashMap<Character, Double> getBackGroundProbs(){ 
		HashMap<Character, Double> result = new HashMap<Character, Double>();
		double sum = 0.0;
		for(Descriptor desc: descriptors){
			for(String segmSeq : desc.getSeqs()){
				for (Character c : segmSeq.toUpperCase().toCharArray()){
					if (c == '.' || c == 'X'){
						continue;
					}
					if (!result.containsKey(c)){
						result.put(c, 0.0);
					}
					result.put(c,(result.get(c) + 1));
					sum++;
				}
			}
		}
		if (sum != 0){
			for(Character c : result.keySet()){
				result.put(c, result.get(c)/sum);
			}
		}
		return result;
	}
}
