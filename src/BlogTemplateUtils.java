import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.FileUtils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import entities.BlogPost;
import entities.Page;


public class BlogTemplateUtils {

	private static final String SINGLEPAGE_TEMPLATE_LOCATION = "singlepage.template";

	public static ArrayList<Page> convertBlogPostToSinglePages(ArrayList<BlogPost> bps) throws IOException {
		String pageTemplateString = FileUtils.getStringFromFile(SINGLEPAGE_TEMPLATE_LOCATION);
		if(pageTemplateString==null) throw new RuntimeException("No singlepages.template file found in CWD.");
		ArrayList<Page> pages = new ArrayList<Page>();
		for (BlogPost blogPost : bps) {
		    MustacheFactory mf = new DefaultMustacheFactory();		
			Mustache mustache = mf.compile(new StringReader(pageTemplateString), "");
		    HashMap<String, Object> scopes = new HashMap<String, Object>();
		    scopes.put("post", blogPost.getPost());
		    scopes.put("attr", blogPost.getMetadata());
		    StringWriter writer = new StringWriter();	    
		    mustache.execute(writer, scopes);
		    writer.flush();
		    pages.add(new Page(blogPost.getFilename(), writer.getBuffer().toString()));
		}
		return pages;
	}

	public static ArrayList<Page> convertBlogPostToPaginatedPages(ArrayList<BlogPost> bps) {
		File[] pagesTemplates = FileUtils.getFilesInDirectory(new File("."), ".*\\.\\d+\\.pagination.template");
		for (File file : pagesTemplates) {
			int paginationNumber = getPaginationNumberFromFilename(file.getAbsolutePath());
			String pageTemplateString = FileUtils.getStringFromFile(file.getAbsolutePath());
			applyPaginationTemplateToBlogPosts(bps, paginationNumber, pageTemplateString);
		}
		return null;
	}

	private static int getPaginationNumberFromFilename(String absoluteFileName) {
		Pattern p = Pattern.compile(".*\\.(\\d+)\\.pagination.template");
		Matcher m = p.matcher(absoluteFileName);
		m.matches();
		String num = m.group(1);
		return Integer.valueOf(num);
	}

	private static void applyPaginationTemplateToBlogPosts(ArrayList<BlogPost> bps, int paginationSize, String pageTemplateString) {
	    int totalPages = (int) Math.ceil((double)bps.size() / (double)paginationSize);
	    MustacheFactory mf = new DefaultMustacheFactory();		
		Mustache mustache = mf.compile(new StringReader(pageTemplateString), "");
		boolean atEndOfPages = false;
		for(int currentPage = 1;!atEndOfPages;currentPage++) {
			List<BlogPost> subList = getSublistForPagination(bps, paginationSize, currentPage);
			HashMap<String, Object> scopes = new HashMap<String, Object>();
			scopes.put("posts", subList);
			scopes.put("num_pages_total", totalPages);
		    scopes.put("num_pages_current", String.valueOf(currentPage));
		    StringWriter writer = new StringWriter();	    
		    mustache.execute(writer, scopes);
		    writer.flush();
			System.out.println(writer.getBuffer().toString());
			if(subList.get(subList.size()-1) == bps.get(bps.size()-1)) atEndOfPages = true;
		}
	}

	private static List<BlogPost> getSublistForPagination( ArrayList<BlogPost> bps, int paginationSize, int currentPage) {
		int startPostNum = (currentPage-1)*paginationSize;
		int endPostNum = currentPage*paginationSize;
		if(endPostNum>bps.size()) endPostNum = bps.size();
		List<BlogPost> subList = bps.subList(startPostNum, endPostNum);
		return subList;
	}


}
