#!/usr/bin/bash

# docker exec -it wordpress-${NUM}-metricbeat bash
# ~/bin/install.bash               
# exit
# docker stop wordpress-${NUM}-metricbeat
# docker start wordpress-${NUM}-metricbeat

rm -f /usr/share/metricbeat/modules.d/beat-xpack.yml
rm -f /usr/share/metricbeat/modules.d/beat-xpack.yml.disabled
cp /root/bin/beat-xpack.yml /usr/share/metricbeat/modules.d/beat-xpack.yml
rm -f /usr/share/metricbeat/modules.d/nginx.yml
rm -f /usr/share/metricbeat/modules.d/nginx.yml.disabled
cp /root/bin/nginx.yml /usr/share/metricbeat/modules.d/nginx.yml
rm -f /usr/share/metricbeat/modules.d/tomcat.yml
rm -f /usr/share/metricbeat/modules.d/tomcat.yml.disabled
cp /root/bin/tomcat.yml /usr/share/metricbeat/modules.d/tomcat.yml
rm -f /usr/share/metricbeat/modules.d/redis.yml
rm -f /usr/share/metricbeat/modules.d/redis.yml.disabled
cp /root/bin/redis.yml /usr/share/metricbeat/modules.d/redis.yml
rm -f /usr/share/metricbeat/modules.d/system.yml
rm -f /usr/share/metricbeat/modules.d/system.yml.disabled
cp /root/bin/system.yml /usr/share/metricbeat/modules.d/system.yml.disabled
rm -f /usr/share/metricbeat/modules.d/docker.yml
rm -f /usr/share/metricbeat/modules.d/docker.yml.disabled
cp /root/bin/docker.yml /usr/share/metricbeat/modules.d/docker.yml.disabled
chown root.root /usr/share/metricbeat/modules.d/*
