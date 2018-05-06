# Tesseract-OCR-Scanner

Tesseract-OCR-Scanner是基于Tesseract-OCR实现的数字自动识别。

实现效果图

<img src="/preview/1.jpg" width="280px"/> <img src="/preview/2.jpg" width="280px"/>
 <img src="/preview/3.jpg" width="280px"/>

<img src="/preview/4.png" width="320px"/>

## 实现说明

具体参考博客：http://blog.csdn.net/qq_17766199/article/details/77963278

## 其他

- 训练数据放在res/raw目录下，需要识别其他语言可另行下载替换。本项目使用的为英文识别训练包。

- 数字识别时，框小一点会好识别。（可以手动调节大小的扫描框）

- 数字识别对于手写体识别效率不高，主要是训练包问题。有需求可自行训练。

## Thanks For

- https://github.com/rmtheis/tess-two
- https://github.com/iluhcm/QrCodeScanner
- https://www.codeproject.com/tips/840623/android-character-recognition
- http://www.cnblogs.com/asingingfish/p/6196638.html?utm_source=itdadao&utm_medium=referral

## License

	Copyright 2017 simplezhli

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
