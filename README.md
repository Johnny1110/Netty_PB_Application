# Netty_PB_Application

<br>

使用 netty 實作一個使用 protobuf 交互資料的網路套件，可以搭配 pbsocket（python） 專案測試。



<br>

---

<br>

## 說明

<br>

先說明一下這個套件的設計需求，我們需要製作一個 java 程式可以跟其他語言實現 socket 資料交互的工具。

涉及到 socket 的部分首先就想到使用 netty，netty 永遠是可以讓人放心的框架選擇。

既然要交互資料，就需要定義傳送的資料格式，大部分人可能會想用 `json` 或 `xml` 等等，netty 已經有實現 `protobuf` 格式的 encoder 與 decoder。所以我們就選擇使用 protobuf 協議實作。

這篇筆記會先介紹一下各個 package 都有甚麼，以及作用。Netty 的知識不會太多做介紹，所以 __看本篇筆記前最好要有一定的 Netty 使用基礎__。

<br>
<br>
<br>
<br>

## 目錄

<br>

* [protobuf 介紹（引用 python pbsocket 文件）](https://github.com/Johnny1110/pbsocket/blob/main/doc/protobuf&varint32)

* [結構總結與類別介紹](./doc/structureSummary&ClassIntroduction)

* [關於 Netty](./doc/aboutNetty)

* [Source Code 解析](./doc/analysisSourceCode/README.md)

* [Demo 演示](./doc/demo/README.md)


