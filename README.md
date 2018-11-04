## 功能简介

这是车辆管理系统的Android端，服务端见：https://github.com/sm1314/VehicleSYS

实现对车辆的电子围栏管理（出围栏报警）、位置、速度、车型等基本信息展示、轨迹展示、超速报警等等。
![车辆管理系统Android端](https://raw.githubusercontent.com/sm1314/VehicleSYS/master/screenshots/screen.png)

## 项目特点


- 1.基于Android端的OpenSteetMap组件开发，不依赖百度/高德/腾讯地图的sdk，可在与互联网隔离的情况下使用。
- 2.支持自定义gis地图瓦片服务器，也支持本地地图，也支持两者混合使用，比如14级清晰度以下使用本地，14级以上请求服务器
- 3.项目适配了50寸+的大屏Android终端，显示效果较好

## API

- 1.车载终端实时上传信息

方式：Get请求， http://服务器地址/{id}/edit？latitude=XXX&longitude=XXX&speed=XXX&angle=XXX&locatetime=XXX

功能：车载终端调用该API实现当前车辆位置、速度、角度等信息的上传。

源码：/app/Http/Controllers/BeiDouController.php 部分

- 2.返回所有车辆实时信息

方式：Get请求， http://服务器地址/     ， 返回值为Json

功能：Android调用该API获取所有车辆的实时信息

源码：/app/Http/Controllers/BeiDouController.php 部分

- 3.返回某辆车的实时信息

方式：Get请求， http://服务器地址/{id}， 返回值为Json

功能：Android调用该API获取车辆ID为{id}车辆的实时信息

源码：/app/Http/Controllers/BeiDouController.php 部分

- 4.返回某辆车的轨迹信息

方式：Get请求， http://服务器地址/trace/{id}， 返回值为Json

功能：Android调用该API获取车辆ID为{id}车辆的轨迹信息

源码：/app/Http/Controllers/BeiDouController.php 部分

## 数据库

数据库依据需求设计三个表，car表存储所有车辆固定信息，如颜色、车型、车牌、ID等。carpts存储车辆轨迹信息，用于车辆当日轨迹信息的显示。carpts_201801XX每日新建一个表，用于存储历史轨迹。


## License

DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
        Version 2, December 2004

Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

Everyone is permitted to copy and distribute verbatim or modified
copies of this license document, and changing it is allowed as long
as the name is changed.

DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

0. You just DO WHAT THE FUCK YOU WANT TO.
