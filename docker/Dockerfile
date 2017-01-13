FROM busybox:latest

# Copy services jar
COPY target/dependency/cerebro-services.jar /opt/cerebro-services/cerebro-services.jar

# Share volume for Java image
VOLUME /opt/cerebro-services/


# Copy dashboard tar
COPY target/dependency/cerebro-dashboard.tar.gz /opt/cerebro-dashboard/cerebro-dashboard.tar.gz
# Extract dashboard from archive
RUN mkdir -p /usr/local/apache2/htdocs
RUN tar xf /opt/cerebro-dashboard/cerebro-dashboard.tar.gz -C /usr/local/apache2/htdocs/
RUN rm /opt/cerebro-dashboard/cerebro-dashboard.tar.gz

# Share volume for httpd image
VOLUME /usr/local/apache2/htdocs/
