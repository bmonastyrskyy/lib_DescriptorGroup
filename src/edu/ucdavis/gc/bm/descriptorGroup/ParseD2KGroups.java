package edu.ucdavis.gc.bm.descriptorGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ParseD2KGroups implements ParseGroups {

	/**
	 * HashMap of mapping residues addresses to fastaNo's for all astral domains
	 */
	@SuppressWarnings("unused")
	private static HashMap<String, HashMap<String, Integer>> hashResMapStr2Int;
	/**
	 * HashMap of mapping fastaNo's to residues' addresses for all astral
	 * domains
	 */
	private static HashMap<String, HashMap<Integer, String>> hashResMapInt2Str;

	/**
	 * hash contains fasta sequences for all astral domains <br>
	 * hash with key - domain; value - fasta sequence
	 */
	private static HashMap<String, String> fastaSeqs;

	/**
	 * hash contains ss sequences for all astral domains <br>
	 * key - domain; value - ss sequence
	 */
	private static HashMap<String, String> SS_Seqs;
	
	private static HashMap<String,String> hashFold = null;

	private File dir = null;

	/**
	 * constructor
	 * 
	 * @param File
	 *            dir
	 */
	public ParseD2KGroups(File dir,
			HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs, HashMap<String, String> SS_Seqs) {
		this.dir = dir;
		ParseD2KGroups.hashResMapStr2Int = hashResMapStr2Int;
		ParseD2KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD2KGroups.fastaSeqs = fastaSeqs;
		ParseD2KGroups.SS_Seqs = SS_Seqs;
	}

	/**
	 * constructor
	 * 
	 * @param File
	 *            dir
	 */
	public ParseD2KGroups(File dir,
			HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs, HashMap<String, String> SS_Seqs, HashMap<String,String> hashFold) {
		this.dir = dir;
		ParseD2KGroups.hashResMapStr2Int = hashResMapStr2Int;
		ParseD2KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD2KGroups.fastaSeqs = fastaSeqs;
		ParseD2KGroups.SS_Seqs = SS_Seqs;
		ParseD2KGroups.hashFold = hashFold;
	}
	
	/**
	 * constructor
	 * 
	 * @param String
	 *            dirName
	 */
	public ParseD2KGroups(String dirName,
			HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs, HashMap<String, String> SS_Seqs) {
		File dir = new File(dirName);
		if (dir.isDirectory()) {
			this.dir = dir;
		}
		ParseD2KGroups.hashResMapStr2Int = hashResMapStr2Int;
		ParseD2KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD2KGroups.fastaSeqs = fastaSeqs;
		ParseD2KGroups.SS_Seqs = SS_Seqs;
	}

	/**
	 * constructor
	 * 
	 * @param String
	 *            dirName
	 */
	public ParseD2KGroups(String dirName,
			HashMap<String, HashMap<String, Integer>> hashResMapStr2Int,
			HashMap<String, HashMap<Integer, String>> hashResMapInt2Str,
			HashMap<String, String> fastaSeqs, HashMap<String, String> SS_Seqs, HashMap<String,String> hashFold) {
		File dir = new File(dirName);
		if (dir.isDirectory()) {
			this.dir = dir;
		}
		ParseD2KGroups.hashResMapStr2Int = hashResMapStr2Int;
		ParseD2KGroups.hashResMapInt2Str = hashResMapInt2Str;
		ParseD2KGroups.fastaSeqs = fastaSeqs;
		ParseD2KGroups.SS_Seqs = SS_Seqs;
		ParseD2KGroups.hashFold = hashFold;
	}
	
	@Override
	public List<Group> parse() throws IOException {
		List<Group> result = new ArrayList<Group>();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().matches("\\S+\\.gr");
			};
		});
		for (File file : files) {
			result.add(parseFile(file));
		}
		return result;
	}

	@Override
	public List<Group> parse(int from, int howMany) throws IOException {
		List<Group> result = new ArrayList<Group>();
		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.getName().matches("\\S+\\.gr");
			};
		});
		// sort files for reproducibility results
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return f1.compareTo(f2);
			}
		});
		int curIndex = 0;
		for (File file : files) {
			if (curIndex >= from && curIndex < from + howMany) {
				result.add(parseFile(file));
			}
			curIndex++;
		}
		return result;
	}

	private Group parseFile(File file) throws IOException {
		Group group = new Group();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine(); // read first line
		String groupName = this.parseDescrName(line);
		group.setName(groupName);
		group.setNameD2K(this.parseGroupNameD2K(line));
		List<Descriptor> descriptors = new ArrayList<Descriptor>();
		Descriptor desc = this.parseRootDescriptorLine(line);
		if (null != desc) {
			descriptors.add(desc);
		}
		while (null != (line = br.readLine())) {
			desc = this.parseDescriptorLine(line);
			if (null != desc) {
				descriptors.add(desc);
			}
		}
		br.close();
		group.setDescriptors(descriptors);
		return group;
	}

	/**
	 * The method extracts descriptor name from <li>root descriptor's line</li><br>
	 * e.g.: d1f74a_ 201-209;3-11 DGAIGSTFN LKGIFSALL #7<br>
	 * or <li>regular descriptor's line</li> e.g.: d1nvra_ 226-253;184-204
	 * IDSAPLALL GIVLTAMLA 0_0:-5_-8 0.477587<br>
	 * 
	 * @param line
	 * @return
	 */
	private String parseDescrName(String line) {
		// d1f74a_ 201-209;3-11 DGAIGSTFN LKGIFSALL #7
		// d1nvra_ 226-253;184-204 IDSAPLALL GIVLTAMLA 0_0:-5_-8 0.477587
		// GroupName is created as name of domain (remove proceeding 'd') + pdb
		// number of the first residue
		// in the second segment (3-11 in the example) increased by 10000 + '_'
		// + number of cluster (7 in the case)
		// the residue with fasta number 3 is translated into address A4_ - so
		// the pdb number is 4
		// finally the group name will be 1f74a_#10004_7
		String[] tokens = line.split("\\s+");
		String dom = tokens[0].substring(1);
		String noClust = "";
		@SuppressWarnings("unused")
		int shift1 = 0; // segment1 shift
		int shift2 = 0; // segment2 shift
		try {
			if (tokens[4].startsWith("#")) {
				noClust = "_" + tokens[4].substring(1);
			} else {
				noClust = "";
			}
			if (tokens[4].startsWith("0_0")) {
				tokens[4] = tokens[4].replaceFirst("0_0:", "");
				String[] shifts = tokens[4].split("_");
				shift1 = Integer.valueOf(shifts[0]);
				shift2 = Integer.valueOf(shifts[1]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// do nothing keep default values
		}
		// get the first number of second segment : e.g. 3 from 201-209;3-11
		String[] tokensA = tokens[1].split(";");
		String[] tokensB = tokensA[1].split("-");
		Integer fastaNo = (Integer.valueOf(tokensB[0]) - shift2 >= 0 ? Integer
				.valueOf(tokensB[0]) - shift2 : Integer.valueOf(tokensB[0]));

		// get pdb address
		String pdbResAddr = hashResMapInt2Str.get(dom).get(fastaNo);
		pdbResAddr = pdbResAddr.substring(1, pdbResAddr.length() - 1);
		int pdbResNo = Integer.valueOf(pdbResAddr);

		return (dom + "#" + (10000 + pdbResNo) + noClust);
	}

	private String parseGroupNameD2K(String line) {
		// d1f74a_ 201-209;3-11 DGAIGSTFN LKGIFSALL #7
		StringBuilder sb = new StringBuilder("");
		String[] tokens = line.split("\\s+");
		sb.append(tokens[0] + ".");
		sb.append(tokens[1].replace(';', '_'));
		try{
			sb.append(tokens[4].replace('#', '.'));
		} catch (ArrayIndexOutOfBoundsException e){
			// do nothing
		}
		return sb.toString();
	}

	/**
	 * The method parses descriptor line:<br>
	 * e.g.: d1nvra_ 226-253;184-204 IDSAPLALL GIVLTAMLA 0_0:-5_-8 0.477587<br>
	 * 
	 * @param line
	 * @return
	 */
	// d1nvra_ 226-253;184-204 IDSAPLALL GIVLTAMLA 0_0:-5_-8 0.477587
	private Descriptor parseDescriptorLine(String line) {
		Descriptor result = new Descriptor();
		// set descriptor name
		String descName = parseDescrName(line);
		result.setName(descName);
		String dom = descName.substring(0, 6);
		String[] tokens = line.split("\\s+");
		// 226-253;184-204
		String[] tokensA = tokens[1].split(";");
		Integer start1 = Integer.valueOf(tokensA[0].replaceFirst("-\\d+", "")); // 226
		Integer start2 = Integer.valueOf(tokensA[1].replaceFirst("-\\d+", "")); // 184
		// 0_0:-5_-8
		tokens[4] = tokens[4].replaceAll("0_0:", "");
		String[] shifts = tokens[4].split("_");
		int shift1 = Integer.valueOf(shifts[0]);
		int shift2 = Integer.valueOf(shifts[1]);
		start1 = start1 - shift1;
		start2 = start2 - shift2;

		int end1 = start1 + tokens[2].length() - 1;
		int end2 = start2 + tokens[3].length() - 1;

		String seq1;
		String seq2;
		try {
			seq1 = fastaSeqs.get(dom).substring(start1, end1 + 1);
			seq2 = fastaSeqs.get(dom).substring(start2, end2 + 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
		String SSeq1;
		String SSeq2;
		try {
			SSeq1 = SS_Seqs.get(dom).substring(start1, end1 + 1);
			SSeq2 = SS_Seqs.get(dom).substring(start2, end2 + 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}

		String startAddr1 = hashResMapInt2Str.get(dom).get(start1);
		String endAddr1 = hashResMapInt2Str.get(dom).get(end1);

		String startAddr2 = hashResMapInt2Str.get(dom).get(start2);
		String endAddr2 = hashResMapInt2Str.get(dom).get(end2);

		Segment segm1 = new Segment();
		segm1.setStart(startAddr1);
		segm1.setStartFastaNo(start1);
		segm1.setEnd(endAddr1);
		segm1.setEndFastaNo(end1);
		segm1.setSeq(seq1);
		segm1.setSSSeq(SSeq1);

		Segment segm2 = new Segment();
		segm2.setStart(startAddr2);
		segm2.setStartFastaNo(start2);
		segm2.setEnd(endAddr2);
		segm2.setEndFastaNo(end2);
		segm2.setSeq(seq2);
		segm2.setSSSeq(SSeq2);

		List<Segment> segments = new ArrayList<Segment>();
		// switch the order of segments
		segments.add(segm2);
		segments.add(segm1);
		result.setSegments(segments);
		try{
			result.setFoldAstral(hashFold.get(dom));
		} catch(NullPointerException e){
			// do nothing i this case
		}
		return result;
	}
	
	private Descriptor parseRootDescriptorLine(String line) {
		// d1f74a_ 201-209;3-11 DGAIGSTFN LKGIFSALL #7
		String [] tokens = line.split("\\s+");
		if (tokens.length == 5){
			line = tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + tokens[3] + " 0_0:0_0 0.0";
		} else {
			line = line + " 0_0:0_0 0.0";
		}
		return this.parseDescriptorLine(line);
	}
}
