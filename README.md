#Sensor Adapter for OGC SOS 1.0.0
##Description
This application is developed to enable a [Davis Vantage Pro 2 Weather Station](http://www.davisnet.com/weather/products/vantage-pro-professional-weather-stations.asp) send observations automatically to an [OGC SOS service](http://www.opengeospatial.org/standards/sos/). The version of the OGC SOS service that is supported is 1.0.0. The reference implementation that was used for testing purposes is the SOS Service by [52North](http://52north.org/communities/sensorweb/sos/index.html). This helper application is developed as part of the EU FP7 project [IDIRA](http://www.idira.eu/) and released from the [NKUA partner](http://www.di.uoa.gr/), [Pervasive Computing Research Group](http://p-comp.di.uoa.gr) as an open source contribution.

This application will be further developed in order to provide a simple way for other sensors to post messages to an OGC SOS service. 

##Prerequisites

###Serial Communication library
1. Download the RxTx library
2. Follow the instructions at [RxTx's wiki](http://rxtx.qbang.org/wiki/index.php/Installation_for_Windows)
	- RXTXcomm.jar goes in \jre\lib\ext (under java)
	- rxtxSerial.dll goes in \jre\bin
	
###DAVIS weather station in Windows OS
1. Unplug the DAVIS weather station
2. Install 592, 593, 600 software versions from DAVIS
	- The 592 version can be found in the accompanying cd
	- The 593 can be found [here](http://www.davisnet.com/support/weather/downloads/software_direct.asp?SoftCat=1&SoftwareID=211)
	- the 600 can be found [here](http://www.davisnet.com/support/weather/downloads/software_direct.asp?SoftCat=1&SoftwareID=214)
3. Plug in the Vantage Pro2 console via the Vantage Pro data logger and connector purchased separately from DAVIS.

##Build
The application is provided with a maven pom script. All additional dependencies are included.