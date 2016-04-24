package org.ht.iops;

import org.ht.iops.bootstrap.IOpsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = IOpsApplication.class)
@ActiveProfiles("test")
public class IOpsApplicationTests {

	public IOpsApplicationTests() {
		System.setProperty("iops.testmode", "true");
	}

	@Test
	public void contextLoads() {
	}
}
