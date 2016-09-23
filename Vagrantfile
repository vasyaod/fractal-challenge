# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"

  config.vm.provider "virtualbox" do |v|
    v.memory = 2024
    v.cpus = 2
  end

  config.vm.provision "shell",
    inline: "sudo add-apt-repository ppa:openjdk-r/ppa -y && sudo apt-get update && sudo apt-get -y install openjdk-8-jdk && sudo update-alternatives --config java"

  config.vm.provision "docker" do |d|
    d.run "rabbitmq:3-management",
    args: "--name some-rabbit -p 5672:5672 -p 15672:15672"
  end
end