package tk.maxuz.blog.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NoteCreation.class, NoteUpdate.class, NoteEditFields.class})
public class TestStarter {
	@BeforeClass
	public static void prepareTest() {
		
	}
	
	@AfterClass
	public static void finilizeTest() {
		
	}
}
