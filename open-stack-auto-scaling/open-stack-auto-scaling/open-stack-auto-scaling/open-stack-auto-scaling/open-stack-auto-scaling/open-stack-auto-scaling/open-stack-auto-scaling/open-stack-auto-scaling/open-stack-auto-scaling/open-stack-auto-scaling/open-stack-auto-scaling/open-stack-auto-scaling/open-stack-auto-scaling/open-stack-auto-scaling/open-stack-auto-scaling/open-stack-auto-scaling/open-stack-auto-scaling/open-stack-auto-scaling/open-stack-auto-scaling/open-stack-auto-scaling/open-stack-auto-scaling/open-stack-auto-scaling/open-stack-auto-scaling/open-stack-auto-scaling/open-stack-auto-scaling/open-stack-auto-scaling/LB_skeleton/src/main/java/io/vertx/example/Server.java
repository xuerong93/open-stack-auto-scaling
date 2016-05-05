package io.vertx.example;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.impl.StringEscapeUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import javax.naming.NamingException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * LB server
 *
 */
public class Server {

    /**
     * Vert.x configuration
     */
    private static Vertx vertx;
    private static HttpClient httpClient;
    private static HttpServer httpServer;

    /**
     * data structure used for LoadBalancer
     */
    private static final int PORT = 80;
    private static List<DataCenterInstance> instances;
    private static ServerSocket serverSocket;
    private static HealthChecker healthChecker;

    /**
     * Main function
     *
     * @param args
     * @throws NamingException
     */
    public static void main(String[] args) throws NamingException {
        // create dataCenterList
        instances = new ArrayList<DataCenterInstance>();
        healthChecker = new HealthChecker(instances);

        // initial server socket
        initServerSocket();

        vertx = Vertx.vertx(new VertxOptions().setWorkerPoolSize(1024));

        // Create http server
        HttpServerOptions serverOptions = new HttpServerOptions();
        httpServer = vertx.createHttpServer(serverOptions);

        // Create Router
        // TODO: implement your LB
        // TODO: add more handlers
        Router router = Router.router(vertx);
        router.route("/add").handler(Server::handleAdd);
        router.route("/remove").handler(Server::handleRemove);
        router.route("/cooldown").handler(Server::handleCoolDown);
        router.route("/check").handler(Server::handleCheck);

        router.route("/").handler(routingContext -> {
            routingContext.response().end("OK");
        });

        // Listen for the request on port 8080
        httpServer.requestHandler(router::accept).listen(8080);

        // open a new thread to run the dispatcher to handle traffic from LG
        Thread launchLoadBalancer = new Thread() {
            public void run() {
                LoadBalancer loadBalancer = new LoadBalancer(serverSocket, instances);
                try {
                    loadBalancer.start();
                } catch (IOException e) {

                }
            }
        };
        launchLoadBalancer.start();
        
        // open a new thread to run the dispatcher to handle traffic from LG
        Thread launchHealthChecker = new Thread(healthChecker) ;
           
        launchHealthChecker.start();
    }

    private static void handleAdd(RoutingContext routingContext)  {
        HttpServerResponse response = routingContext.response().putHeader("Content-Type", "text/plain; charset=utf-8");
        // to get argument from http request
        String dnsName = routingContext.request().getParam("ip");
        // TODO: implement the add handler
        System.out.printf("%s. Get new instance [IP: %s]\n", TimeManager.PrintCurrentTime(), dnsName);
        
        DataCenterInstance newMachine = new DataCenterInstance("new", dnsName);
        if(newMachine.isHealthy()){
			System.out.printf("%s. Add new instance\n", TimeManager.PrintCurrentTime());
			instances.add(newMachine);
		}else{
			System.out.printf("%s. New instance unhealthy\n", TimeManager.PrintCurrentTime());
		}

        // close the connection and send the response body
        response.end("success\n");
    }
    
    private static void handleRemove(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response().putHeader("Content-Type", "text/plain; charset=utf-8");
        String dnsName = routingContext.request().getParam("ip");
        System.out.printf("%s. Removing instance [IP: %s]\n", TimeManager.PrintCurrentTime(), dnsName);
        
        Iterator<DataCenterInstance> it = instances.iterator();
        
        while(it.hasNext()){
        	DataCenterInstance machine = it.next();
        	if(machine.getUrl().equals(dnsName)){
        		it.remove();
                System.out.printf("%s. Removed\n", TimeManager.PrintCurrentTime(), dnsName);
        	}
        }
        // close the connection and send the response body
        response.end("success\n");
    }
    private static void handleCoolDown(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response().putHeader("Content-Type", "text/plain; charset=utf-8");
        String strCool = routingContext.request().getParam("cooldown");
        int coolDown = Integer.parseInt(strCool);
        System.out.printf("%s. Setting cool down [Val: %d]\n", TimeManager.PrintCurrentTime(), coolDown);
        
        healthChecker.setCoolDown(coolDown);
        // close the connection and send the response body
        response.end("success\n");
    }

    private static void handleCheck(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response().putHeader("Content-Type", "text/plain; charset=utf-8");
        System.out.printf("%s. Checking...\n", TimeManager.PrintCurrentTime());
        StringBuilder res = new StringBuilder();
        for(DataCenterInstance machine:instances){
        	res.append("," + machine.getUrl() );
        }
        res.append('\n');
        // close the connection and send the response body
        String output = res.substring(1);
        System.out.printf("%s. Check out [%s]\n", TimeManager.PrintCurrentTime(), output);
        response.end(output);
    }
    /**
     * Initialize the socket on which the Load Balancer will receive requests from the Load Generator
     */
    private static void initServerSocket() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("ERROR: Could not listen on port: " + PORT);
            e.printStackTrace();
            System.exit(-1);
        }
    }

}

