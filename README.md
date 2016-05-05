#open-stack-auto-scaling
Start building our own cloud and provide services for application owners to use. Specifically, we will build a private cloud using the OpenStack platform and add the capabilities for a simple AutoScaling group and a Load Balancer. 

We have divided the project into two parts:

* set up an OpenStack cluster on a single large EC2 VM. This will allow you to learn some simple networking and virtualization terminology, and start to 
explore different tools to build virtual machines and virtual networks
* create a load balancer from the perspective of a cloud provider that allows cloud application developers (like you were a week ago) to route HTTP traffic between their OpenStack VMs (OVMs)
* create a simple scaling group that triggers scale-in and scale-out events based on configurable parameters and self-designed policy(CPU or network utilization of the participating OpenStack VMs)

##Running an OpenStack cluster on a single EC2 node
###stack.sh
By default, this script installs 5 of the 7 core OpenStack components (all but Neutron and Swift) based on the settings specified in local.conf. You can use this script to choose the components to run, configure the network settings, etc. You must run this script every time you wish to modify your cluster configuration.
###unstack.sh
As you would imagine, unstack.sh stops all the services launched by stack.sh.

![architect_diagram](https://s3.amazonaws.com/openstackasglbp1/sys_architect)

##Implementing a Load Balancer
Load balancer will comprise of two pieces:

* A daemon, service or application running on the EC2 host that tracks the state of all running load balancers and issues commands to individual load balancers.
* The actual network component that performs the load balancing between OpenStack VMs.

##Implementing an AutoScaling Group
An AutoScaling group creates a number of instances of a particular flavor based on a specific image. When launched, it creates the minimum number of instances specified. Then, it periodically evaluates the performance of all instances over a fixed period and checks if the average performance is greater than the scale-out threshold or less than the scale-in threshold. Then, based on the utilization it horizontally scales the number of instances within the cluster by adding or removing n servers at a time.

Policy makes decision based on parameters provided in `./configure`
<ol type="i">
    <li> 
    ASG\_IMAGE, ASG\_FLAVOR, ASG\_NAME:
    These three parameters are used to launch your ASG instances.
    </li>
    <li>
    LB\_IPADDR:
    The IP address of the LB associated to your ASG.
	</li>
	<li>
	CPU\_UPPER/LOWER\_TRES:
	The CPU thresholds for scaling out/in.
	</li>
	<li>
	MIN/MAX\_INSTANCE:
	The minimum and maximum instance number of your ASG.
	</li>
	<li>
	EVAL\_PERIOD, EVAL\_COUNT:
	For every EVAL_PERIOD seconds, your ASG should check the CPU usage of your 	instances. If the average CPU usage hits the scale in/out thresholds for 	EVAL_COUNT times successively, your ASG should consider taking an action.
	</li>
	<li>
	COOL_DOWN:
	After every scale in/out actions, your ASG should wait for COOL_DOWN seconds 	to take another action, no matter how many evaluations hit the thresholds 	during this period. Also note that your ASG should wait for COOL_DOWN 	seconds at the beginning, waiting for the initial instances to boot up.	</li>
	<li>
	DELTA:
	For every scaling action, you increase/decrease the size of your ASG by DELTA.
	</li>
</ol>



