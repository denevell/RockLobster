package org.denevell.rocklobster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.denevell.rocklobster.blogposts.BlogPost;
import org.denevell.rocklobster.templates.infrastructure.PageTemplateFactory;
import org.denevell.rocklobster.utils.GitUtils;
import org.denevell.rocklobster.utils.LogUtils;
import org.eclipse.jgit.storage.file.FileRepository;



public class Main {
	
	private static Logger LOG = LogUtils.getLog(Main.class);
	private static  String URL_REMOTE_REPO; //i.e "git@github.com:denevell/BlogPosts.git";
	private static final String DIR_LOCAL_REPO = "git-repo";
	public static String CONTENT_FILE_SUFFIX = ".md";
	public static String OUTPUT_DIR = "";

	public static void main(String[] s) throws Exception {
		// Set up vars
		parseConfigProperties();		
		// Blog it up
		String absolutePath = new File(DIR_LOCAL_REPO).getAbsolutePath(); // For location of files
		FileRepository fileGitRepo = new FileRepository(absolutePath+"/.git"); // To reference our git repo
		LOG.info("## Cloning (or pulling existing) git repo");
		GitUtils.cloneOrPullExistingRepository(URL_REMOTE_REPO, fileGitRepo); 
		LOG.info("## Parsing blog files");
		List<BlogPost> bps = new BlogPostParsing(fileGitRepo).parseFilesInDirectory(absolutePath);
		LOG.info("## Converting blog files from markdown");
		BlogMarkdownUtils.convertMDToHTML(bps);
		LOG.info("## Creating single and paginated pages from factories onto filesystem");
		BlogFileCreationUtils.createPosts(bps, PageTemplateFactory.getFactories());
	}

	private static Properties parseConfigProperties() throws FileNotFoundException, IOException {
		Properties defaultProps = new Properties();
		FileInputStream in = new FileInputStream("rock.lobster");
		defaultProps.load(in);
		in.close();
		URL_REMOTE_REPO = defaultProps.getProperty("git_repo");
		OUTPUT_DIR = defaultProps.getProperty("output_dir");
		String suffix = defaultProps.getProperty("file_suffix");
		CONTENT_FILE_SUFFIX= (suffix!=null) ? suffix.trim() : CONTENT_FILE_SUFFIX;
		return defaultProps;
	}
	
}
