package be.ceau.podcastfinder.cl;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import picocli.CommandLine;

public class PodcastFinderClTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
    @Test
    public void noArgsCausesExit() {
    	exit.expectSystemExitWithStatus(0);
    	PodcastfinderCl.main(new String[0]);
    }
    
    @Test
    public void exportTest() {
    	String[] args = new String[] {"--export=json"};
    	PodcastfinderArguments arguments = CommandLine.populateCommand(new PodcastfinderArguments(), args);
    	Assert.assertEquals("json", arguments.getExport());
    	Assert.assertNotEquals("opml", arguments.getExport());
   }

}