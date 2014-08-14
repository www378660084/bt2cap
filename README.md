bt2cap
======

将蓝牙串口数据(hci/bcsp),转换为wireshark能够识别的hcidump格式文件


运行
=======
1.将串口数据文件拖放到run目录的bat文件上,即可在同目录生成.cap文件
2.安装wireshark,打开生成的cap文件

数据格式
======
[sec.usec]TX/RX: 串口数据

例:
TX: C0 40 41 00 7E DA DC ED ED A9 7A C0
[10.10]TX: C0 40 41 00 7E DA DC ED ED A9 7A C0
RX: C0 40 41 00 7E DA DC ED ED A9 7A C0