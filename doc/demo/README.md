# Demo 演示

<br>

---

<br>

以下展示一下簡單的 Demo 展示：

<br>

首先需要寫一個 `AbstractApplicationBooter` 的繼承類。

<br>

`TestApplicationBooter` 類：

<br>

```java
public class TestApplicationBooter extends AbstractApplicationBooter {

    public TestApplicationBooter(RecordReader<ProtoData.Record> recordReader) {
        super(recordReader);
    }

    @Override
    public void runClient(String remoteHostName, int remotePort) {
        System.out.println("please run the client socket, remoteHostName: " + remoteHostName + " remotePort: " + remotePort);
    }
}
```

<br>

`runClient()` 部分可以寫 `ProcessBuilder` 用 CMD 的方式啟動 python Client，這邊我打算手動啟動，所以就只印出 `remoteHostName` 與 `remotePort` 資訊方便我們手動操作。

<br>
<br>
<br>
<br>

然後我們還需要寫一個寫出資料的邏輯，所以需要繼承 `ChannelActiveListener`，在連線激活時處理：

<br>

`PbRecordSender` 類別：

```java
public class PbRecordSender implements ChannelActiveListener {
    
    // 非同步 Queue，用於儲存待處理資料
    private ConcurrentLinkedQueue<ProtoData.Record> recordQueue;

    public PbRecordSender(){
        recordQueue = new ConcurrentLinkedQueue<ProtoData.Record>();
    }
    
    // 提供一個新增 Record 進消息佇列的方法
    public boolean addRecordToQueue(ProtoData.Record record){
        return recordQueue.offer(record);
    }
    
    // 連線激活時的通知方法
    @Override
    public void noticed(ServerObjectHandler serverObjectHandler) {
        while (true){
            ProtoData.Record record = recordQueue.poll();
            if (record == null){
                continue;
            }
            serverObjectHandler.sendData(record);
            if(record.getSignal().equals(ProtoData.Record.Signal.STOP)){
                // 當收到 STOP 訊號時結束此 Lintener 處理。
                break;
            }
        }
    }
}
```

<br>
<br>
<br>
<br>

測試使用：

<br>

```java
public class TestServer {

    private ApplicationBooter<ProtoData.Record> booter;

    private ProtoData.Record testRecord;

    private ProtoData.Record testStopRecord;

    private PbRecordSender pbRecordSender;
    
    // 準備前置作業
    @Before
    public void prepareServerSocket(){
        RecordReader<ProtoData.Record> reader = System.out::println;
        // 把 RecordReader 塞到 TestApplicationBooter 中
        this.booter = new TestApplicationBooter(reader);
        // 把先前時做的 PbRecordSender 加入到 boot 的 ChannelActiveListenerList 中
        this.pbRecordSender = new PbRecordSender();
        this.booter.addChannelActiveListener(this.pbRecordSender);
    }
    
    // 準備測試資料
    @Before
    public void prepateTestData(){
        ProtoData.PbData data1 = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.STRING)
                .setBinaryData(ByteString.copyFrom("Hello World!".getBytes()))
                .build();

        ProtoData.PbData data2 = ProtoData.PbData.newBuilder()
                .setDataType(ProtoData.PbData.DataType.INT)
                .setBinaryData(ByteString.copyFrom(ByteBuffer.allocate(4).putInt(1695609641).array()))
                .build();

        this.testRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.NODE)
                .putColumn("data1", data1)
                .putColumn("data2", data2)
                .build();

        this.testStopRecord = ProtoData.Record.newBuilder()
                .setSignal(ProtoData.Record.Signal.STOP)
                .build();
    }
    
    // 測試
    @Test
    public void testServerSocket() throws InterruptedException {

        booter.startUp();
        pbRecordSender.addRecordToQueue(testRecord);
        pbRecordSender.addRecordToQueue(testRecord);
        pbRecordSender.addRecordToQueue(testRecord);
        pbRecordSender.addRecordToQueue(testStopRecord); // 結束時記得要傳一個 stop 訊號

        while (true){
            Thread.sleep(10000); // 10 秒內沒有連線成功就結束
            if (!booter.isConnected()){
                break;
            }
        }

        booter.forceStopJob(); // 關閉應用

    }

}
```

<br>

應用啟動後看一下 console：

<br>

```java
please run the client socket, remoteHostName: kubernetes.docker.internal remotePort: 58407
```

<br>

需要在 10 秒內啟動 Client 連線到 Server 端，不然應用會自己關閉。

<br>

pbsocket 部分測試：

<br>

```py
class MyClient(PbClientSocket):
    def processRecord(self, record):
        print(record)
        print('---'*30)
        sleep(1)
        self.sendRecord(record)


if __name__ == '__main__':
    myClient = MyClient(host="kubernetes.docker.internal", port=58407)
    myClient.startUp()
```

<br>

記得 port 部分是會隨機分配的，所以要根據 Server 啟動時的 console 輸出而改變。

<br>

啟動 client 後就可以看到從 Server 端傳來的資料，`sleep(1)` 模擬處理資料時的時間，然後把資料原封不動的傳回 Server：

<br>

python console：

<br>

```
column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

------------------------------------------------------------------------------------------
column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

------------------------------------------------------------------------------------------
column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

------------------------------------------------------------------------------------------

Process finished with exit code 0

```

<br>

java 端 console：

<br>

```
please run the client socket, remoteHostName: kubernetes.docker.internal remotePort: 58407
connected with : /127.0.0.1:58835
column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

column {
  key: "data1"
  value {
    binaryData: "Hello World!"
  }
}
column {
  key: "data2"
  value {
    dataType: INT
    binaryData: "e\020\363)"
  }
}

Reviced close signal, notice endListeners...
Trying to close DMServer...
Trying to close client channel forcibly...
Client channel closed successfully.
Main channel closed successfully.
DMServer gracefully shutdown.

Process finished with exit code 0
```