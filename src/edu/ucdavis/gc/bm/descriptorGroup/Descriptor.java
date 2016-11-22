package edu.ucdavis.gc.bm.descriptorGroup;

import java.util.ArrayList;
import java.util.List;

public class Descriptor {
	/**
	 * name of descriptor
	 */
	private String name;
	/**
	 * list of segments - objects of class Segment 
	 */
	private List<Segment> segments;
	
	/**
	 * class according to ASTRAL
	 */
	private String foldAstral ;
	
	public Descriptor(){
		
	}
	// set and get methods
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void  setSegments(List<Segment> segments){
		this.segments = segments;
	}
	
	public List<Segment> getSegments(){
		return this.segments;
	}
	/**
	 * 
	 * @return number of segments in descriptor
	 */
	public int getNumberSegments(){
		return this.segments.size();
	}
	/**
	 * 
	 * @return list of segments' sequences
	 */
	public List<String> getSeqs(){
		List<String> result = new ArrayList<String>();
		for(Segment segment : segments){
			result.add(segment.getSeq());
		}
		return result;
	}
	/**
	 * 
	 * @return list of edges of segments in format 'start1 - end1'
	 */
	public List<String> getEdges(){
		List<String> result = new ArrayList<String>();
		for(Segment segment : segments){	
			result.add(segment.getStart() + " - " + segment.getEnd());
		}
		return result;
	}
	/**
	 * 
	 * @return total number of residues in all segments 
	 */
	public int getNumberResidues(){
		int result = 0;
		for(String seq : this.getSeqs()){
			result += seq.length();
		}
		return result;
	}
	/**
	 * sets parameter classAstral - classification according to ASTRAL
	 * @param classAstral
	 */
	public void setFoldAstral(String foldAstral){
		this.foldAstral = foldAstral;
	}
	
	/**
	 * 
	 * @return class according to ASTRAL classification
	 */
	public String getFoldAstral(){
		return this.foldAstral;
	}
	
	/**
	 * 
	 * @return domain name of the descriptor
	 */
	public String getDomainName(){
		int index = this.name.indexOf("#");
		return this.name.substring(0, index);
	}
	@Override
	public String toString(){
		StringBuilder result = new StringBuilder();
		result.append(String.format("%-12s    ",this.name));
		for(Segment segm : this.segments){
			result.append(String.format("%-12s  %s     " ,segm.getStart() + "-" + segm.getEnd(),  segm.getSeq().toUpperCase() ));
			
		}
		result.append(this.foldAstral);
		return result.toString();
	}
	
	public String toStringAsAssign(String groupName){
		StringBuilder result = new StringBuilder();
		result.append(groupName + ".ser");
		result.append(String.format("\t%s\t", this.name));
		StringBuilder before = new StringBuilder("");
		StringBuilder after = new StringBuilder("");
		for(Segment segm : this.segments){
			before.append(String.format("%s-%s;", segm.getStartFastaNo(), segm.getEndFastaNo()));
			after.append(String.format("%s-%s;" ,segm.getStart(), segm.getEnd()));
		}
		result.append(before + "/" + after);
		result.append("\t-1.0");
		result.append("\t:" + this.name.substring(0, 6));
		return result.toString();
	}
	
	
	public String toStringAsAssign(String groupName, Double hmmScore, Double rmsd){
		StringBuilder result = new StringBuilder();
		result.append(groupName + ".ser");
		result.append(String.format("\t%s\t", this.name));
		StringBuilder before = new StringBuilder("");
		StringBuilder after = new StringBuilder("");
		for(Segment segm : this.segments){
			before.append(String.format("%s-%s;", segm.getStartFastaNo(), segm.getEndFastaNo()));
			after.append(String.format("%s-%s;" ,segm.getStart(), segm.getEnd()));
		}
		result.append(before + "/" + after);
		result.append("\t" + hmmScore);
		result.append("\t:" + "d" + this.name.substring(0, 6));
		result.append("\t:" + rmsd);
		return result.toString();
	}
}
