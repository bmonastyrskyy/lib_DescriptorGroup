package edu.ucdavis.gc.bm.descriptorGroup;

import java.io.IOException;
import java.util.List;

public interface ParseGroups {
	/**
	 * The method parses data source (file/directory)<br>
	 * and return list of descriptor groups. 
	 * @return
	 * @throws IOException
	 */
	public List<Group>  parse()  throws IOException;
	/**
	 * The method parses data source (file/directory)<br>
	 * selecting <span>HowMany</span> groups starting from <span>from</span> index.
	 * @param from
	 * @param howMany
	 * @return
	 * @throws IOException
	 */
	public List<Group>  parse(int from, int howMany)  throws IOException;
}
