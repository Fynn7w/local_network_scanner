# Local Network Scanner (Java)

## Overview

This project is a simple **local network reconnaissance tool written in Java**.
It can scan a subnet for active hosts and perform a basic **port scan** on a target IP.
Based on the detected open ports and other information (TTL, services, banners), the program attempts to **estimate the operating system** of the device.

The scanner was built using core Java networking classes such as **Socket**, **InetAddress**, and basic I/O utilities.

---

## Features

### IP Scan

The program can scan a subnet and detect active hosts.

Information collected:

* Active IP addresses
* TTL value
* Basic OS guess based on TTL
* MAC address (via ARP lookup)

### Port Scan

When port scan mode is enabled, the program checks common ports associated with different operating systems and services.

Detected information:

* Open ports
* Service banners
* HTTP server headers (if available)

### OS Guessing

The scanner estimates the operating system based on:

* Open ports
* TTL value
* Known service patterns

Example output:

```
port 22 is open
banner: SSH-2.0-OpenSSH_9.8

port 445 is open

random os guessing :
windows probability : 20%
linux probability : 20%
macos probability : 60%

device uses airplay ports --> could be an apple device
```

---

## Ports Checked

### Windows related ports

* 445 (SMB)
* 139 (NetBIOS)
* 3389 (RDP)

### Linux / Server related ports

* 22 (SSH)
* 25 (SMTP)
* 2049 (NFS)
* 80 (HTTP)
* 443 (HTTPS)
* 5900 (VNC)

### macOS related ports

* 631 (IPP / printing)
* 548 (AFP)
* 7000 (AirPlay)
* 5000 (AirPlay)
* 7001 (AirPlay mirroring)

---

## Usage

### Compile

```
javac app.java
```

### Run IP Scan

```
java app 192.168.2.
```

This scans the subnet:

```
192.168.2.1 - 192.168.2.254
```

---

### Run Port Scan

```
java app -p 192.168.2.10
```

This performs a **port scan on the specific host**.

---

## How It Works

### IP Discovery

Hosts are checked using:

```
InetAddress.isReachable()
```

### Port Scanning

Ports are tested using a **TCP socket connection**:

```
Socket.connect()
```

If the connection succeeds, the port is considered open.

### Banner Grabbing

For some services, the scanner reads the first response line from the server to detect the running service.

### HTTP Detection

For ports 80 and 443, the scanner sends a basic HTTP request and extracts the **Server header**.

---

## Requirements

* Java 17+ recommended
* Network access to the target subnet
* Some functions (ARP, ping) require system commands available on the host OS

---

## Disclaimer

This tool is intended **only for educational purposes and for scanning networks you own or have permission to test**.

Unauthorized scanning of networks may violate local laws or network policies.

---

## Possible Improvements

Future improvements could include:

* Multithreaded scanning (much faster)
* Better OS fingerprinting
* Vendor lookup for MAC addresses
* Export results to a log file
* More advanced service detection
* Improved error handling

---

## Author
Fyn7w
Local Java network scanner project.
