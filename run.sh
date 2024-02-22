#!/bin/bash

if ! systemctl is-active --quiet mariadb; then
    # Start mariadb if it is not running
    sudo systemctl start mariadb
fi

# Run the Spring Boot application
mvn spring-boot:run
