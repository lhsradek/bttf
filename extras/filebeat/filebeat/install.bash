#!/usr/bin/bash

# docker exec -it wordpress-${NUM}-filebeat bash
# ~/bin/install.bash 
# exit
# docker stop wordpress-${NUM}-filebeat
# docker start wordpress-${NUM}-filebeat

rm -f /usr/share/filebeat/modules.d/nginx.yml
rm -f /usr/share/filebeat/modules.d/nginx.yml.disabled
cp /root/bin/nginx.yml /usr/share/filebeat/modules.d/nginx.yml
chown root.root /usr/share/filebeat/modules.d/*
