
# Spring-Boot Metrics-Demo
This is a very simple [Spring-Boot](https://spring.io/projects/spring-boot)
application that demonstrates usage of [Micrometer](https://micrometer.io) 
together with [Prometheus](https://prometheus.io) and 
[Grafana](https://grafana.com/grafana/). Additionally, this demo uses
[Consul](https://www.consul.io) as a service discovery,
[Fabio](https://fabiolb.net) as load balancer and [Gatling](https://gatling.io)
for load testing the application. This software-stack is used as an example,
other service discoveries, load balancers or load testing tools are possible
and the choice depends on the environment and personal preferences.

On a Mac, one way to install all these tools uses homebrew:
- Install Consul using `brew install consul`
- Install Fabio using `brew install fabio`
- Install Prometheus using `brew install prometheus`
- Install Grafana using `brew install grafana`

After all these tools are installed, they can be started using homebrew or
by simply starting the tool using the command line:
- Start Consul using `brew services start consul`
- Start Fabio using `fabio`
- Start Prometheus using `prometheus --config.file=config-files/prometheus/prometheus.yml`
- Start Grafana using `brew services start grafana`

Prometheus is started using the configuration file, that is included in this 
repository, since this tells Prometheus to use Consul as a service discovery.

As soon as all these services are running, their state may be viewed 
using these URLs:
- Consul: `http://localhost:8500`
- Fabio: `http://localhost:9998`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

Now, 1 to n instances of the Spring-Boot-Metrics-Demo application can be 
started on any local ports, e.g. on ports `8100` and `8101`. Since the 
application instances will register themselves at Consul, the demo will run
using any unused ports with as many instances you like to test.

After the Metrics-Demo application instances are up and running, visit the 
Consul, Fabio and Prometheus pages to see, that everything works fine.

The 2 dashboards from this repository (`config-files/grafana-dashboards/jvm.json`
and `config-files/grafana-dashboards/metrics-demo.json`) should now be imported
into Grafana.

Now, you start the Gatling load test using `gradlew gatlingRun`. The load test
will run for several minutes or hours (depending on the setup in the scala 
class `MetricsDemoSimulation`). During load testing, grafana will show the
live action on your Metrics-Demo instances.

Have fun!

