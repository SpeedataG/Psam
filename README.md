# Psam
-  导入依赖库


**AndroidStudio** build.gradle中的dependencies中添加

```
 dependencies {
    compile 'com.speedata:deivice:1.4'
    compile 'com.speedata:psam:1.4'
  }
```
**Eclipse** 需导入libs库 LibDevice 和 LibIdentity
依赖以上两个lib库  运行时编译即可

1. initDev 初始化设备
1. PsamPower 软使能
1. WriteCmd 写指令
1. resetDev 硬件gpio复位
1. releaseDev 释放设备


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


-  初始化设备


|函数原型|void initDev(String serialport, int braut, DeviceControl.PowerType power_typeint, Context context, int ...  gpio )throws IOException;	                                   |
-------    |-------
|功能描述  |指定参数初始化模块|
|参数描述  |String serialport 串口号 |
|参数描述  |int braut 波特率 |
|参数描述  |DeviceControl.PowerType power_typeint 上电类型 |
|参数描述  |Context context 上下文|
|参数描述  |int ...  gpio 上电gpio |
|返回类型  |失败抛出异常|




|函数原型|void initDev(Context context) throws IOException;	                                   |
-------    |-------
|功能描述  |自动初始化模块|
|参数描述  |Context context 上下文|
|返回类型  |失败抛出异常|

**初始化示例**

```

 //自动初始化模块
   psam.initDev(this);
```

-  使能卡

函数原型|byte[] PsamPower(IPsam.PowerType type)	                                   |
-------    |-------
|功能描述  |使能卡|
|参数描述  |IPsam.PowerType 卡1或者卡2|
|返回类型  |byte[] 上电返回的数据  |


-  发送指令

|函数原型|byte[] WriteCmd(byte[] data, PowerType type) throws UnsupportedEncodingException                                   |
-------    |-------
|功能描述  |发送指令|
|参数描述  |byte[] data 发送的数据|
|参数描述  |IPsam.PowerType type 卡槽|
|返回类型  |byte[] 模块返回数据  |

-  硬件复位

|函数原型|void resetDev(DeviceControl.PowerType type,int Gpio)	                                   |
-------    |-------
|功能描述  |输入指定参数进行硬件gpio复位|
|参数描述  |DeviceControl.PowerType 上电类型|
|参数描述  |int Gpio|
|返回类型  |无  |


|函数原型|void  resetDev()	                                   |
-------    |-------
|功能描述  |硬件复位gpio 按照自动匹配参数 给电平复位 |
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