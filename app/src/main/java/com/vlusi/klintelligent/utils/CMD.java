package com.vlusi.klintelligent.utils;

/**
 * Created by qi022 on 2017/7/8.
 */
public class CMD {
    // pragma mark - 指令分类
    /**
     * 获取
     */
    public final static byte CMD_READ = 0x01;
    /**
     * 写入、设置
     */
    public final static byte CMD_WRITE = 0x02;
    /**
     * 纯指令
     */
    public final static byte CMD_COMMAND = 0x03;

    // pragma mark - 操作指令
    /**
     * 保留
     */
    public final static byte OPT_RETAIN_0 = 0x00;
    /**
     * 产品类型
     */
    public final static byte OPT_ProgramState = 0x01;
    /**
     * 小车ID
     */
    public final static byte OPT_ID = 0x02;
    /**
     * 硬件版本(固件版本)
     */
    public final static byte OPT_HARDWARE_VERSION = 0x03;
    /**
     * 软件版本
     */
    public final static byte OPT_SOFTWARE_VERSION = 0x04;
    /**
     * 骑行sudu模式
     */
    public final static byte OPT_RideSpeed_Mode = 0x05;

    /**
     * 以上4项
     */
    public final static byte OPT_1_TO_5 = 0x06;
    /**
     * 保留
     */
    public final static byte OPT_RETAIN_7 = 0x07;

    /**
     * 发送请求数据流
     */
    public final static byte OPT_SEND_BYTES_REQUEST = 0x50;
    /**
     * 发送数据流
     */
    public final static byte OPT_SEND_BYTES = 0x51;

    /**
     * 设置自动发送
     */
    public final static byte OPT_AUTO_SEND = 0x60;
    /**
     * 停止自动发送
     */
    public final static byte OPT_STOP_SEND = 0x61;

    /**
     * 批量查询
     */
    public final static byte OPT_READ_BATCH = (byte) 0xF0;

    // pragma mark - 纯指令
    /**
     * 恢复出厂设置
     */
    public final static byte OPT_RESET = 0x01;
    /**
     * 关机
     */
    public final static byte OPT_SHUTDOWN = 0x02;
    /**
     * 立即强制断电
     */
    public final static byte OPT_FORCE_SHUTDOWN = 0x03;
    /**
     * 遥控指令
     */
    public final static byte OPT_CONTROL = 0x10;
    /**
     * 确认
     */
    public final static byte OPT_SURE = 0x20;
    /**
     * 取消
     */
    public final static byte OPT_CANCEL = 0x21;

    // pragma mark -遥控指令
    // **** 暂未定义 *****

    // pragma mark - 产品类型
    /**
     * 独轮平衡车
     */
    public final static byte PARAM_PRODUCT_SINGLE_WHEEL = 0x01;
    /**
     * 两轮平衡车
     */
    public final static byte PARAM_PRODUCT_DOUBLE_WHEEL = 0x02;

    // pragma mark - LED方案分类
    /**
     * 预设方案
     */
    public final static byte PARAM_LEDTYPE_SYSTEM = 0x01;
    /**
     * 用户方案
     */
    public final static byte PARAM_LEDTYPE_USER = 0x02;

	/*
     * 车->客户端指令
	 */

    // pragma mark - 回指令分类
    // 应答
    public final static byte CMD_RET_RESPONSE = 0x01;
    /**
     * 准备数据传输
     */
    public final static byte CMD_RET_PREPARE_DATA = 0x02;
    /**
     * 数据传输
     */
    public final static byte CMD_RET_SEND_DATA = 0x03;
    /**
     * 推送消息类(超速、低电、故障、危险、其他)
     */
    public final static byte CMD_RET_PUSH_DATA = 0x04;

    // pragma mark - 回复状态
    /**
     * OK、确定、同意、成功
     */
    public final static byte STATE_OK = 0x01;
    /**
     * NO、否定、拒绝、失败
     */
    public final static byte STATE_NO = 0x02;
    /**
     * 正在执行
     */
    public final static byte STATE_ING = 0x03;
    /**
     * 请求重发
     */
    public final static byte STATE_RESEND = 0x04;

    // pragma mark -回复失败的原因
    /**
     * 未知原因
     */
    public final static byte ERROR_UNKNOWN = 0x01;
    /**
     * 当前状态不允许此操作
     */
    public final static byte ERROR_NOT_ALLOW = 0x02;
    /**
     * 指令数据流格式不对
     */
    public final static byte ERROR_FORM_ERROR = 0x03;
    /**
     * 需要回复的数据总长超过单次发送长度上限
     */
    public final static byte ERROR_LENGTH_ERROR = 0x04;
    /**
     * 其他原因(后续字节描述)
     */
    public final static byte ERROR_OTHER = 0x05;

    // pragma mark -推送消息分类
    /**
     * 超速
     */
    public final static byte PUSH_LOWSPEED = 0x01;
    /**
     * 低电
     */
    public final static byte PUSH_LOWPOWER = 0x02;
    /**
     * 故障
     */
    public final static byte PUSH_ERROR = 0x03;
    /**
     * 未知危险
     */
    public final static byte PUSH_UNKNOWN_DANGER = 0x04;
    /**
     * 未知异常
     */
    public final static byte PUSH_UNKNOWN_EXCEPTION = 0x05;

    // 按照 IEEE754 标准
    // pragma mark - 数据类型以及长度
    public final static byte DATATYPE_NIL = 0x00;
    public final static byte DATATYPE_CHAR = 0x01;
    public final static byte DATATYPE_SHORT = 0x02;
    public final static byte DATATYPE_INT = 0x04;
    public final static byte DATATYPE_LONG = 0x08;
    public final static byte DATATYPE_FLOAT = 0x14;
    public final static byte DATATYPE_DOUBLE = 0x18;
    public final static byte DATATYPE_STRING = 0x20;
    public final static byte DATATYPE_FLOW = 0x30;
    public final static byte DATATYPE_HEX = 0x7D;


    public final static byte DATATYPE_CHAR_LEN = 0x1;
    public final static byte DATATYPE_SHORT_LEN = 0x2;
    public final static byte DATATYPE_byte_LEN = 0x4;
    public final static byte DATATYPE_LONG_LEN = 0x8;
    public final static byte DATATYPE_FLOAT_LEN = 0x4;
    public final static byte DATATYPE_DOUBLE_LEN = 0x8;

    // pragma mark - 底层打包/解包协议：协议使用的特殊字符
    /**
     * 起始标志
     */
    public final static byte STR = (byte) 0xAA;
    /**
     * ESC 转义符
     */
    public final static byte ESC = (byte) 0xAB;
    /**
     * 结束标志
     */
    public final static byte END = (byte) 0xAC;

    /**
     * 起始标志
     */
    public final static byte STR_ESC = (byte) 0xA1;
    /**
     * ESC 转义符
     */
    public final static byte ESC_ESC = (byte) 0xA2;
    /**
     * 结束标志
     */
    public final static byte END_ESC = (byte) 0xA3;

    // pragma mark - other
    /**
     * 单次物理数据传输量暂定为 256 字节以内
     */
    public final static byte PROTACL_MAX_BYTE = (byte) 0xFF;
    /**
     * 针头长度
     */
    public final static byte PROTACL_HEARD_LEN = 0x03;

    public final static byte Product_Car1_Run = (byte) 0xA1;
    public final static byte Product_Car1_Update = (byte) 0xA2;

    public final static byte RideMode_Child = 0x00;
    public final static byte RideMode_Adult = 0x01;

    public final static byte RideMode_Comfort = 0x00;
    public final static byte RideMode_Sport = 0x01;

    // new
    public final static byte RideMode_Open = 0x01;
    public final static byte RideMode_Close = 0x00;

    public final static byte Command_update = (byte) 0xA5;
    public final static byte Command_updateOK = (byte) 0xA6;

    public final static byte Command_dev_main = 0x01;
    public final static byte Command_dev_sub = 0x02;

    public final static byte Command_AjustingStart = 0x31;
    public final static byte Command_AjustingOK = 0x32;

    public final static byte Command_GasStart = 0x18;
    public final static byte Command_GasOK = 0x19;
    public final static byte Command_StartRemoteControl = 0x10;

    public final static byte Command_StopRemoteControl = 0x12;

    public final static byte Command_RemoteControl_Send = 0x11;

    /**
     * 云台相关
     */

    //	  拍照 录像
    public final static byte NOTIFY_CAPTURE = 0x10;

    //    开始
    public final static byte NOTIFY_SWITCH_START = 0x11;
    //    停止录像
  /*  public final static byte NOTIFY_RECORD_END = 0x12;*/
    //    切换摄像头
    public final static byte NOTIFY_SWITCH_CAMERA = 0x13;
    //获取SN
    public static final byte SN = 0X71;
    //获取硬件版本
    public static final byte FIRMWARE_VERSION = 0x72;
    //获取电池容量
    public static final byte BATTERY_CAPACITY = 0x19;
    //获取软件版本
    public static final byte SOFTWARE_VERSION = 0x73;
    //程序运行状态
    public static final byte PROGRAM_RUNNING_STATE = 0x01;
    //批量查询
    public static final byte BATCHQUERY= (byte) 0x85;
    //设置俯仰轴锁定
    public static final byte SET_PITCH_AXIS_LOCK= (byte) 0x80;
    public static final byte PANORAMIC = (byte) 0x90;
    public static final byte SHOT = 0x04;
    public static byte Client= (byte) 0XF0;
}
