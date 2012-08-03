构造一些实用脚本.
充当代码生成器,脚本辅助等功用
一,android:安卓开发工具
1.CopyResource:在工程间拷贝/移动android res下面的资源,并且一并拷贝其依赖项.
例如在layout文件中依赖了drawable,则拷贝该layout会连同该drawable一起拷贝.
2.Px2DP:假设hdpi分辨率,迭代res目录批量将px替换为dp/sp.
3.SelectorMaker:根据drawable规范的前后缀命名自动生成selector的xml文件.
二,luni:通用扩展
BetterRegular:包含eval能力的替换.构思来自RegularBuddy
Downloader:代替linux的wget.今后加入断点续传
Encryptor:基于位与方式的简单加密工具
Zipper:简单粗暴的解压缩工具
三,other:其它
ProjectEncodeConvertor:根据chardet探测编码,然后批量替换项目文件为utf-8并打印输出
四,gen:代码生成器
JsonClassGenerator:通过json生成java bean的class声明代码。尚未实现