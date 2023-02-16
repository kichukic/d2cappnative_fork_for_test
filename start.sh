#!/bin/bash

pm2 start app.mjs
pm2 logs
bash ./automateExecHandle.sh