package org.denevell.rocklobster.plugins;

import java.util.Collections;
import java.util.List;
import java.lang.Math;

import org.denevell.rocklobster.blogposts.BlogPost;
import org.denevell.rocklobster.plugins.infrastructure.Plugin;
import org.denevell.rocklobster.utils.MetadataUtils;

public class AllTagsPlugin implements Plugin {

	private static int MAX_OCCURRENCES = 40; // TODO: Pass in as arguments to plugin

	@Override
	public String getOuput(List<BlogPost> bps, String[] args) { 
		if(args.length!=6) throw new RuntimeException("Tags plugin needs five arguments: wrapperStart, wrapperEnd, elementStart, elementEnd, inbetweenElement and addition to occurrences replacement. Separated by '||'.");
		String wrapperStart = args[0];
		String wrapperEnd = args[1];
		String elementStart = args[2];
		String elementEnd= args[3];
		String elementInBetween = args[4];
		int addingToOcurrences= Integer.valueOf(args[5]);
		
		List<String> tagsIncDuplicated = MetadataUtils.getValuesOfMetadata("tags", bps);
		String[] tags = MetadataUtils.getDistinctValuesOfMetadata("tags", bps);
		String totalTagsOutput = "";
		for (String tag : tags) {
			if(tag==null || tag.length()==0) continue;
			String elementStartReplaced = elementStart.replaceAll("\\[tagname\\]", tag);
			elementStartReplaced = elementStartReplaced.replaceAll("\\[occurrences\\]", getOccurrences(tagsIncDuplicated, tag, addingToOcurrences, MAX_OCCURRENCES));
			String inbetweenElement = "";
			if(totalTagsOutput.length()!=0) {
				inbetweenElement=elementInBetween;
			} 
			totalTagsOutput += inbetweenElement+elementStartReplaced+tag+elementEnd;
		}
		return wrapperStart+totalTagsOutput+wrapperEnd;
	}

	private String getOccurrences(List<String> obs, String o, int addition, int max) {
		int num = Collections.frequency(obs, o);
		num = Math.min(num, max);
		return String.valueOf(num+addition);
	}

	@Override
	public String getName() {
		return "all-tags";
	}

}
