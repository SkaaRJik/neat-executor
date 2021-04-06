package ru.filippov.neatexecutor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.filippov.neatexecutor.samba.SambaWorker;

@SpringBootTest
class NeatExecutorApplicationTests {

	@Autowired
	private Runner runner;

	@Autowired
	private SambaWorker sambaWorker;

	@Test
	void contextLoads() throws Exception {
		runner.run();
	}

	@Test
	void setSambaWork() throws Exception {


		byte[] bytes = sambaWorker.readFile("data.csv");
		sambaWorker.writeBytesArray("test.csv", bytes);
	}

}
