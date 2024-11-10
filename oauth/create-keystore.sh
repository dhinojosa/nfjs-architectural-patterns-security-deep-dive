#!/bin/bash

keytool -genkeypair -alias mykey -keyalg RSA -keystore keystore/mykeystore.jks -storepass farmer$knitter
