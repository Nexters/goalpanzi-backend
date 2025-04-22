#!/bin/bash

if ! command -v unzip &> /dev/null; then
  sudo apt-get update
  sudo apt-get install -y unzip
fi

curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install