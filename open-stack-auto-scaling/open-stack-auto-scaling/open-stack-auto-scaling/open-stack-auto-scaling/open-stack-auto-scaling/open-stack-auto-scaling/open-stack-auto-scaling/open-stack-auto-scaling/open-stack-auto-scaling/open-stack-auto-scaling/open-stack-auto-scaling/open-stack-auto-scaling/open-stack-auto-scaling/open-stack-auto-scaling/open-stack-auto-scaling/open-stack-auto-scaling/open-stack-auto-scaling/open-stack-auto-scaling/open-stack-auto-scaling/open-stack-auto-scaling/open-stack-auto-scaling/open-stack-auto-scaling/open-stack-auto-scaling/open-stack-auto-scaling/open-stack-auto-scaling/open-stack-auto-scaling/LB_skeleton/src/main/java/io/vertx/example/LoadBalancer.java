package io.vertx.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

public class LoadBalancer {
	private static final int THREAD_POOL_SIZE = 4;
	private final ServerSocket socket;
	private final List<DataCenterInstance> instances;
	private static HealthChecker healthChecker;

	public LoadBalancer(ServerSocket socket, List<DataCenterInstance> instances) {
		this.socket = socket;
		this.instances = instances;
	}

	// Complete this function
	public void start() throws IOException {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		int index = 0;
		while (true) {
			//  TODO: send the request in round robin manner as well as handle health check

			if (instances.size() == 0) {
				continue;
			}
			index = (index + 1) % instances.size();
			Runnable requestHandler = new RequestHandler(socket.accept(), instances.get(index));
			executorService.execute(requestHandler);
		}
	}
}
