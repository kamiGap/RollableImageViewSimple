# RollableImageView

支持图片沿着四个方向滚动的view

![效果](rollImageView效果.gif)

## 接入

复制 RollableImageView.java 和 attr 内相关参数到项目内即可

## 使用

xml参数

| 参数              | 默认值       | 备注                                  |
| ----------------- | ------------ | ------------------------------------- |
| riv_rolldirection | Right        | 滚动方向 可选参数 right left top down |
| riv_repeatTime    | 0            | 滚动次数 0为无限循环                  |
| riv_duration      | 400         | 持续时间                              |
| riv_auto_play     | 是否自动播放 | 是否自动播放                          |

代码调用

设置图片

`setImageBitmap(Bitmap bitmap)` 

有限次数播放

`startRoll(int repeatTime, int duration, int rollDirection)`

无限次播放

`startRollInfinite(int oneTimeDuration, int rollDirection)`
