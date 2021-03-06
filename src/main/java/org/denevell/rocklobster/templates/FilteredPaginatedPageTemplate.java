package org.denevell.rocklobster.templates;

import java.util.List;
import java.util.Map;

import org.denevell.rocklobster.blogposts.BlogPost;

public class FilteredPaginatedPageTemplate extends PaginatedPageTemplate {

	private String mMetadataValue;
	private String mMetadataKey;

	public FilteredPaginatedPageTemplate(List<BlogPost> unfilteredBlogpost, String templateFilename, List<BlogPost> blogPosts, String key, String value, int currentPage, int totalPages) {
		super(unfilteredBlogpost, templateFilename, blogPosts, currentPage, totalPages);
		this.mMetadataKey = key;
		this.mMetadataValue = value;
	}
	
	@Override
	protected String getGeneratedPaginationFilename(int currentPage, String relativeFileName) {
		String fn = super.getGeneratedPaginationFilename(currentPage, relativeFileName);
		String replace = fn.replace("["+mMetadataKey+"]", mMetadataValue);
		return replace;
	}
	
	@Override
	public Map<String, Object> getTemplateScopes() {
		Map<String, Object> scopes = super.getTemplateScopes();
		scopes.put("metadata_filter", mMetadataValue);
		return scopes;
	}

}
