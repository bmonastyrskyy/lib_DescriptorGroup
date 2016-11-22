package edu.ucdavis.gc.bm.descriptorGroup;
/**
 * class to work with segments of descriptors
 * @author bohdan
 *
 */
  public class Segment { 
	
	/**
	 * residue address of left edge of the segment
	 */
	private String start;
	/**
	 * residue internal number of left edge of the segment
	 * the counting number according to the enumeration of fasta sequence
	 */
	private Integer startFastaNo;
	
	/**
	 * residue address of the right edge of the segment 
	 */
	private String end;
	
	/**
	 * residue internal number of right edge of the segment
	 * the counting number according to the enumeration of fasta sequence
	 */
	private Integer endFastaNo;

	/**
	 * sequence of the segment
	 */
	private String seq;
	
	/**
	 * secondary structure sequence
	 */
	private String ssSeq;
	
	/**
	 * The address of the last residue before the gap. 
	 */
	private String lastResBeforeGap;
	
	
	/**
	 * The address of the first residue after gap. 
	 */
	private String firstResAfterGap; 
	
	
	public Segment(){
		
	}
	
	// get and set methods
	public void setStart(String start){
		this.start = start;
	}
	
	public String getStart(){
		return this.start;
	}
	
	public void setStartFastaNo(int startFastaNo){
		this.startFastaNo = startFastaNo; 
	}
	
	public Integer getStartFastaNo(){
		return this.startFastaNo;
	}
	
	public void setEnd(String end){
		this.end = end;
	}
	
	public String getEnd(){
		return this.end;
	}
	
	public void setEndFastaNo(int endFastaNo){
		this.endFastaNo = endFastaNo; 
	}
	
	public Integer getEndFastaNo(){
		return this.endFastaNo;
	}

	public void setSeq(String seq){
		this.seq = seq;
	}
	
	public String getSeq(){
		return this.seq;
	}
	
	public String getSSSeq(){
		return this.ssSeq;
	}
	
	public void setSSSeq(String ssSeq){
		this.ssSeq = ssSeq;
	}
	
	public void setLastBeforeGap(String lastBeforeGap){
		this.lastResBeforeGap = lastBeforeGap;
	}
	
	public String getLastBeforeGap(){
		return this.lastResBeforeGap;
	}
	
	public void setFistAfterGap(String firstAfterGap){
		this.firstResAfterGap = firstAfterGap;
	}
	
	public String getFirstAftergap(){
		return this.firstResAfterGap;		
	}
}
