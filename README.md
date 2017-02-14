# Psam
-  导入依赖库


**AndroidStudio** build.gradle中的dependencies中添加

```
 dependencies {
    compile 'com.speedata:deivice:1.1'
    compile 'com.speedata:psam:1.1'
  }
```
**Eclipse** 需导入libs库 LibDevice 和 LibIdentity
依赖以上两个lib库  运行时编译即可

1. initDev 初始化设备
1. PsamPower 软使能
1. sendData 发送数据
1. startReadThread 开启读线程
1. stopReadThread 停止读线程


-  枚举 PowerType

|字段|说明|
|:----    |:-------    |
|Psam1  |卡1    |
|Psam1 |卡2 |

------------

-  IPsam 获取操作实例
```
{
  IPsam psam = PsamManager.getPsamIntance();
  }
```


-  软使能

函数原型|void PsamPower(PowerType type)                                  |
-------    |-------
|功能描述  |模块软使能|
|参数描述  |PowerType type 卡1/卡2 |
|返回类型  |无|



上电结果需接受广播
 POWER_ACTION 广播
 boolean POWER_RESULT 上电结果
 int POWER_TYPE 1 卡1  2卡2


-  初始化设备


|函数原型|void initDev(String serialport, int braut, DeviceControl.PowerType power_typeint, Context context, int ...  gpio );	                                   |
-------    |-------
|功能描述  |模块软使能|
|参数描述  |String serialport 串口号 |
|参数描述  |int braut 波特率 |
|参数描述  |DeviceControl.PowerType power_typeint 上电类型 |
|参数描述  |Context context 上下文|
|参数描述  |String serialport 串口号 |
|参数描述  |int ...  gpio 上电gpio |
|返回类型  |b|
**初始化示例**

```

 //调用主板和外部扩展上电示例
   psam.initDev(serialport, baurate, DeviceControl.PowerType.MAIN_AND_EXPAND,
                this, 88, 2);
```

-  复位

|函数原型|void resetDev(DeviceControl.PowerType type,int Gpio)	                                   |
-------    |-------
|功能描述  |发送数据|
|参数描述  |DeviceControl.PowerType 上电类型|
|参数描述  |int Gpio|
|返回类型  |无  |


-  发送数据

|函数原型|int sendData(byte[] data,IPsam.PowerType type)	                                   |
-------    |-------
|功能描述  |发送数据|
|参数描述  |byte[] data 发送的数据|
|参数描述  |IPsam.PowerType type 卡槽|
|返回类型  |int  大于0代表写入成功  |

-  开启读线程

|函数原型|void  startReadThread(Handler handler)	                                   |
-------    |-------
|功能描述  |开启读线程|
|参数描述  |Handler handler  用户需实现handleMessage 处理接受到的数据|
|返回类型  |无  |

-  开启读线程

|函数原型|void  stopReadThread()	                                   |
-------    |-------
|功能描述  |停止读线程|
|参数描述  |无|
|返回类型  |无  |

-  释放设备

函数原型|void releaseDev() throws IOException	                                   |
-------    |-------
|功能描述  |释放设备|
|参数描述  |无  程序退出时需调用此方法|
|返回类型  |无  |



北京思必拓科技股份有限公司

网址 http://www.speedata.cn/

技术支持 电话：155 4266 8023

QQ：2480737278