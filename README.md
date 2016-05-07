# VMMigrationController
An Northbound Application of OpenDayLight to optimize the VM migration flow
it provides a restful api for network manager to do the flow optimization
the restful url is http://{webserviceIp}:{port}/VMMigrationController/MigrationService/Optimize/
the HTTP method will be post, here is the input template
{
  "srcIp":{srcIp},
  "destIp":{destIp},
  "controllerIp":{controllerIp}
}
Copyright:XiaocongDong 20160507