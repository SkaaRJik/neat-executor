package ru.filippov.neatexecutor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NeatExecutorApplicationTests {

	@Autowired
	private Runner runner;

	@Test
	void contextLoads() throws Exception {
		runner.run();
	}

}
