# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|
  config.vm.box = "ubuntu/trusty64"

  config.vm.provider "virtualbox" do |v|
    v.memory = 1024
    v.cpus = 2
  end

  config.vm.provision "shell",
    inline: "sudo apt-get -y install default-jre"

  config.vm.provision "docker" do |d|
    d.run "rabbitmq:3-management",
    args: "--name some-rabbit -p 5672:5672 -p 15672:15672"
  end
end