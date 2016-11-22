package edu.ucdavis.gc.bm.descriptorGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ParseD3KGroups implements ParseGroups {
	

	/**
	 * HashMap of mapping residues addresses to fastaNo's for all astral domains
	 */
	private static  HashMap<String, HashMap<String, Integer>> hashResMapStr2Int;
	/**
	 * HashMap of mapping fastaNo's to residues' addresses for all astral
	 * domains
	 */
	private static HashMap<String, HashMap<Integer, String>> hashResMapInt2Str;

	/**
	 * hash contains fasta sequences for all astral domains <br>
	 * hash with key - domain; value - fasta sequence
	 */
	@SuppressWarnings("unused")
	private static HashMap<String, String> fastaSeqs;

	/**
	 * hash contains ss sequences for all astral domains <br>
	 * key - domain; value - ss sequence
	 */
	private static HashMap<String, String> SS_Seqs;

	/**
	 * problem domains
	 */
	private static TreeSet<String> problemDomains;
	

	private File file;

	public ParseD3KGroups(File file, HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs,
			HashMap<String, String> SS_Seqs, TreeSet<String> problemDomains) {
		this.file = file;
		ParseD3KGroups.hashResMapStr2Int =  hashResMapStr2Int;
		ParseD3KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD3KGroups.fastaSeqs = fastaSeqs;
		ParseD3KGroups.SS_Seqs = SS_Seqs;
		ParseD3KGroups.problemDomains = problemDomains;
	}

	public ParseD3KGroups(String filePath, HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs,
			HashMap<String, String> SS_Seqs, 
			TreeSet<String> problemDomains) {
		this.file = new File(filePath);
		ParseD3KGroups.hashResMapStr2Int =  hashResMapStr2Int;
		ParseD3KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD3KGroups.fastaSeqs = fastaSeqs;
		ParseD3KGroups.SS_Seqs = SS_Seqs;
		ParseD3KGroups.problemDomains = problemDomains;
	}

	public ParseD3KGroups(String dirName, String fileName, HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs,
			HashMap<String, String> SS_Seqs, 
			TreeSet<String> problemDomains) {
		File dir = new File(dirName);
		this.file = new File(dir, fileName);
		ParseD3KGroups.hashResMapStr2Int =  hashResMapStr2Int;
		ParseD3KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD3KGroups.fastaSeqs = fastaSeqs;
		ParseD3KGroups.SS_Seqs = SS_Seqs;
		ParseD3KGroups.problemDomains = problemDomains;
	}

	/**
	 * the parser return the list of groups starting from the group sequential number and no more then howmany.
	 * @param from
	 * @param howMany
	 * @return
	 * @throws IOException
	 */
	public List<Group> parse(int from, int howMany) throws IOException {
		List<Group> result = new ArrayList<Group>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		int count = 0;
		String line;
		List<String> groupLines = new ArrayList<String>(); // contains all lines
															// corresponding to
															// one group
		// boolean fStartGroup = false;
		boolean firstGroup = true;
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals("")) { // if empty line - continue
				continue;
			}
			if (line.startsWith("GROUP")) { // line starts with "GROUP" -
											// indicator to start groupLines
				if (count < from){
					count++;
					continue;
				}
				if (count == from + howMany){
					break;
				}
				count++;
				// fStartGroup = true;
				if (firstGroup == false) { // if it is not first group
					if (groupLines.size()>0){
						Group group = this.parseGroup(groupLines);
						if (group != null){
							result.add(group); // add group to
																	// result
						}
					}
				} else {
					firstGroup = false;					
				}
				groupLines.clear(); // clear groupLines
				groupLines.add(line); // add "GROUP" line
				continue;
			}
			if (count >= from){
				groupLines.add(line);
			}
		}
		if (groupLines.size() > 0){
			Group group = this.parseGroup(groupLines);
			if (group != null){
				result.add(group); // add last group to result
			}
		}
		reader.close();
		return result;
	}
	
	/**
	 * parse file with groups of descriptors
	 * 
	 * @return list of groups
	 * @throws IOException
	 */
	public List<Group> parse() throws IOException {
		List<Group> result = new ArrayList<Group>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		List<String> groupLines = new ArrayList<String>(); // contains all lines
															// corresponding to
															// one group
		// boolean fStartGroup = false;
		boolean firstGroup = true;
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals("")) { // if empty line - continue
				continue;
			}
			if (line.startsWith("GROUP")) { // line starts with "GROUP" -
											// indicator to start groupLines
				// fStartGroup = true;
				if (firstGroup == false) { // if it is not first group
					Group group = this.parseGroup(groupLines);
					if (group != null){
						result.add(group); // add group to
																// result
					}
				} else {
					firstGroup = false;
				}
				groupLines.clear(); // clear groupLines
				groupLines.add(line); // add "GROUP" line
				continue;
			}
			groupLines.add(line);
		}
		Group group = this.parseGroup(groupLines);
		if (group != null){
			result.add(group); // add last group to result
		}
		reader.close();
		return result;
	}

	private Group parseGroup(List<String> groupLines) {
		Group result = new Group();
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		for (String line : groupLines) {
			if (line.startsWith("GROUP")) {
				result.setName(this.parseGroupLine(line));
			} else {
				Descriptor curDesc = null;
				try{
					curDesc = this.parseDescriptorLine(line);
				} catch (ArrayIndexOutOfBoundsException e){
					System.err.println(line);
					throw e;
				}
				if (curDesc != null) {
					descriptors.add(curDesc);
				}
			}
		}
		if (descriptors.size() == 0) {
			return null;
		}
		result.setDescriptors(descriptors);
		return result;
	}

	private String parseGroupLine(String line) { // "GROUP: 1ub3a_#203: 5"
		String[] tokens = line.trim().split("\\s+");
		return tokens[1].substring(0, tokens[1].indexOf(":"));
	}

	private Descriptor parseDescriptorLine(String line) { // "1c1yb_#109  97-103   AVFRLLH  107-111  GKKAR  125-131  ELQVDFL...  d.15.1.5 Human"
		String domain = line.substring(0, line.indexOf("#"));
		if (problemDomains.contains(domain)) {
			return null;
		}
		Descriptor result = new Descriptor();
		String[] tokens = line.split("\\s+");
		result.setName(tokens[0]);
		List<Segment> segments = new ArrayList<Segment>();
		int i = 1;
		while (!tokens[i].matches("[a-g]\\..*")) { 	// loop until meet "d.15.1.5"
													// - indicator of ASTRAL
													// fold
			// patch to handle with incorrect format
			if (tokens[i].matches("[\\.-]{0,1}[0-9]+[A-Z]*[\\.-][\\.-]{0,1}[0-9]+[A-Z]*")){
				tokens[i] = tokens[i].replaceAll("\\.", "-");
			}
			if (tokens[i].matches("-{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}--{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}") && tokens[i+1].matches("\\.*[A-Za-z]+\\.*")){
				//System.out.println(tokens[i] + "\t" + tokens[i+1]);
				Segment segment = parseSegment(domain,tokens[i],tokens[i+1]);
				segments.add(segment);
				i += 2;
			} else if (tokens[i].matches("-{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}--{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}") && tokens[i+1].matches("-{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}--{0,1}[A-Z]{0,1}[0-9]+[A-Z]{0,1}") && tokens[i+2].matches("\\.*[A-Za-z]+\\.*")){
				i += 3;
				return null;
			}
			
			
		}
		// test for overlapped segments
		if(isOverlappedSegments(segments)){
			return null;
		}
		result.setSegments(segments);
		result.setFoldAstral(tokens[i]);
		return result;
	}

	private Segment parseSegment(String domain, String bounds, String sequence) {
		String chain = domain.substring(4, 5).toUpperCase();
		Segment segment = new Segment();
		int indexSeparator = bounds.indexOf('-', 1);
		String start = bounds.substring(0, indexSeparator);
		String end = bounds.substring(indexSeparator + 1);
		String startAddr = start;
		String endAddr = end;
		if (!chain.equals(".")) {
			startAddr = chain + startAddr;
			endAddr = chain + endAddr;
		}
		if (!startAddr.matches(".*[a-zA-Z]$")) {
			startAddr = startAddr + "_";
		}
		if (!endAddr.matches(".*[a-zA-Z]$")) {
			endAddr = endAddr + "_";
		}

		// adjust the bounds of segments 
		Pattern patternBefore = Pattern.compile("[a-z]+[A-Z]");
		Matcher matcherBefore = patternBefore.matcher(sequence);
		Pattern patternAfter = Pattern.compile("[A-Z][a-z]+");
		Matcher matcherAfter = patternAfter.matcher(sequence);
		//find the first occurrence of the patternBefore
		int beforeLength = 0;
		if(matcherBefore.find()){
			beforeLength = matcherBefore.end() - matcherBefore.start() - 1;			
		}
		int afterLength = 0;
		while(matcherAfter.find()){
			afterLength = matcherAfter.end() - matcherAfter.start() - 1;
		}
		int startFastaNo;
		int endFastaNo;
		try{
			startFastaNo = hashResMapStr2Int.get(domain).get(startAddr);
			endFastaNo = hashResMapStr2Int.get(domain).get(endAddr);
		} catch (NullPointerException e){
			System.err.println(domain + " " + startAddr);
			throw e;
		}
		
		// adjust indexes
		String newStart = hashResMapInt2Str.get(domain).get(startFastaNo - beforeLength);
		String newEnd = hashResMapInt2Str.get(domain).get(endFastaNo + afterLength);
		// if one of the residue addresses is actually corresponds to the gap 
		// residue address of a gap is noted as _0_ 
		if (null == newStart || null == newEnd || newStart.equalsIgnoreCase("_0_") || newEnd.equalsIgnoreCase("_0_")){
			sequence = sequence.replaceAll("[a-z]", ".");
			newStart = hashResMapInt2Str.get(domain).get(startFastaNo);
			newEnd = hashResMapInt2Str.get(domain).get(endFastaNo);
		} else {
			startFastaNo -= beforeLength;
			endFastaNo += afterLength;
		}
		
		segment.setStartFastaNo(startFastaNo);
		segment.setEndFastaNo(endFastaNo);
		
		if (!chain.equals(".")){
			newStart = newStart.substring(1);
			newEnd = newEnd.substring(1);
		}
		newStart = newStart.replaceAll("_", "");
		newEnd = newEnd.replaceAll("_", "");
		segment.setStart(newStart);
		segment.setEnd(newEnd);
		segment.setSeq(sequence);
		try{
			segment.setSSSeq(SS_Seqs.get(domain).substring(startFastaNo, endFastaNo + 1));
		} catch (StringIndexOutOfBoundsException e){
			segment.setSSSeq(SS_Seqs.get(domain).substring(startFastaNo));
		}
		
		return segment;
	}
	/**
	 * The method tests for overlap of the segments
	 * @param segments
	 * @return
	 */
	private boolean isOverlappedSegments(List<Segment> segments){
		boolean result = false;
		for ( int i = 0 ; i < segments.size() - 1; i++){
			if (segments.get(i).getEndFastaNo() >= segments.get(i+1).getStartFastaNo()){
				result = true;
				break;
			}
		}
		return result;
	}
}