# 結構總結與類別介紹

<br>

---

<br>

在這個部分著重來介紹一下應用的結構與每個類別的職責工作。

<br>

## Package 分類

<br>

* [booter](#1)

* [handler](#2)

* [listener](#3)

* [processor](#4)

* [proto](#5)

<br>
<br>
<br>
<br>

<div id="1">

## booter

<br>

booter package 裡面有一個介面與一個抽象類別。

* interface：[`ApplicationBooter`](../../src/main/java/com/frizo/lab/netty/pb/app/booter/ApplicationBooter.java)

* abstract class：[`AbstractApplicationBooter`](../../src/main/java/com/frizo/lab/netty/pb/app/booter/AbstractApplicationBooter.java)

<br>

抽象類別 `AbstractApplicationBooter` 繼承自 `ApplicationBooter`，實現了介面定義的大部分方法。只留下一個抽象方法給使用套件時繼承實作：

 `public abstract void runClient(String remoteHostName, int remotePort);` 

 <br>

 主要的 Application 啟動關閉等控制邏輯都寫在個 package 中。

<br>
<br>
<br>
<br>

<div id="2">

## handler

<br>

handler package 裡面有 2 個 class。

* class： [`ServerProtoBufInitializer`](../../src/main/java/com/frizo/lab/netty/pb/app/handler/ServerProtoBufInitializer.java)

* class： [`ServerObjectHandler`](../../src/main/java/com/frizo/lab/netty/pb/app/handler/ServerObjectHandler.java)

<br>

這兩個類別都是 Netty 的 `ChannelInboundHandler` 繼承類。寫過 Netty 的話應該從類別命名上可以推斷出作用。

`ServerProtoBufInitializer` 裡配置了以 protobuf 協議為基礎的 channel 配置資訊。在建構 ServerBootstrap 時加入並建構，在啟動階段會將自己從 channelList 中移除。

`ServerObjectHandler` 是 protobuf 資料讀入寫出邏輯，在建構 `ServerBootstrap` 時放入 `ServerProtoBufInitializer` 一併建構。

<br>
<br>
<br>
<br>

<div id="3">

## listener

<br>

handler package 裡面有 3 個 interface。

* interface：[`ApplicationListener`](../../src/main/java/com/frizo/lab/netty/pb/app/listener/ApplicationListener.java)

* interface：[`ChannelActiveListener`](../../src/main/java/com/frizo/lab/netty/pb/app/listener/ChannelActiveListener.java)

* interface：[`ProcessEndListener`](../../src/main/java/com/frizo/lab/netty/pb/app/listener/ProcessEndListener.java)

<br>

`ChannelActiveListener` 與 `ProcessEndListener` 繼承自 `ApplicationListener`。

之後還需要加任何種類 Listener Interface 都可以寫在這個 package 裡。

`ChannelActiveListener` 是在與 Client 端連線後的觸發的監聽器。

`ProcessEndListener` 是在與 Client 端連線後接收到 `STOP` 訊號後觸發的監聽器。



<br>
<br>
<br>
<br>

<div id="4">

## processor

<br>

processor package 目前只有一個 interface。

* interface：[`RecordReader`](../../src/main/java/com/frizo/lab/netty/pb/app/processor/RecordReader.java)

<br>

這個 reader 介面需要使用時實作，主要在裡面寫資料的讀取邏輯。在 Server 運行過程中的資料讀取邏輯部分通通都會交由這個介面的實現來執行。

<br>
<br>
<br>
<br>

<div id="5">

## proto

<br>

proto package 放置 protobuf 工具編譯過後的 ProtoData 文件，裡面放置一個 class。

* class：[`ProtoData`](../../src/main/java/com/frizo/lab/netty/pb/app/proto/ProtoData.java)

<br>

ProtoData.java 是由 [ProtoData.proto](../../src/main/resources/ProtoData.proto) 文件編譯而來的。



