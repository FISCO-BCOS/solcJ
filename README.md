# solcJ
`solcJ`提供了`Java`编译`solidity`合约文件的接口，`solidity`编译器`solc`可以参考: https://github.com/ethereum/solidity

## 支持平台

- linux
  - x64
  - aarch64
- mac
- windows

## 支持版本

`solcJ`支持三个版本的`solidity`编译器版本
- 0.4.25
- 0.5.2
- 0.6.10
- 0.8.11
- 0.8.26

## 使用

- `Gradle`
    ```shell
    # 0.4.25 版本
    compile group: 'org.fisco-bcos', name: 'solcJ-0.4.25', version: '1.0.0'

    # 0.5.2 版本
    compile group: 'org.fisco-bcos', name: 'solcJ-0.5.2', version: '1.0.0'

    # 0.6.10 版本
    compile group: 'org.fisco-bcos', name: 'solcJ-0.6.10', version: '1.0.0'
  
    # 0.8.11 版本
    compile group: 'org.fisco-bcos', name: 'solcJ-0.8.11', version: '1.0.0'
  
    # 0.8.26 版本
    compile group  'org.fisco-bcos', name: 'solcJ-0.8.26', version: '1.0.0'
  
    # Linux专属版本，支持所有solidity版本
    compile group  'org.fisco-bcos', name: 'solcJ-linux', version: '1.0.0'
    
    # macOS专属版本，支持所有solidity版本
    compile group  'org.fisco-bcos', name: 'solcJ-mac', version: '1.0.0'
  
    # Windows专属版本，支持所有solidity版本
    compile group  'org.fisco-bcos', name: 'solcJ-win', version: '1.0.0'
  
    # 所有系统架构通用版本，支持所有solidity版本
    compile group  'org.fisco-bcos', name: 'solcJ', version: '1.0.0'
    ```

- `Maven`
  ```shell
  # 0.4.25 版本
  <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-0.4.25</artifactId>
        <version>1.0.0</version>
    </dependency>

  # 0.5.2 版本
  <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-0.5.2</artifactId>
        <version>1.0.0</version>
    </dependency>

   # 0.6.10 版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-0.6.10</artifactId>
        <version>1.0.0</version>
    </dependency>
  
    #0.8.11 版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-0.8.11</artifactId>
        <version>1.0.0</version>
    </dependency>
    
    #0.8.26 版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-0.8.26</artifactId>
        <version>1.0.0</version>
    </dependency>
   
    # Linux专属版本，支持所有solidity版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-linux</artifactId>
        <version>1.0.0</version>
    </dependency>
  
    # macOS专属版本，支持所有solidity版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-mac</artifactId>
        <version>1.0.0</version>
    </dependency>
  
    # Windows专属版本，支持所有solidity版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ-win</artifactId>
        <version>1.0.0</version>
    </dependency>
  
    # 所有系统架构通用版本，支持所有solidity版本
    <dependency>
        <groupId>org.fisco-bcos</groupId>
        <artifactId>solcJ</artifactId>
        <version>1.0.0</version>
    </dependency>
  ```
  
## 接口
### 编译
```
public static Result SolidityCompiler::compile(File source, boolean sm, boolean combinedJson, Option... options)
        throws IOException;
public static Result SolidityCompiler::compile(byte[] source, boolean sm, boolean combinedJson, Option... options)
        throws IOExceptio;
```

参数:
* File source : `solidity`文件文件路径
* byte[] resource : `solidity`文件内容 
* boolean sm: 国密开关
* combinedJson: 
* Option ... options: `solc`编译参数
  * `Options.ABI`: 相当于`solc --abi`
  * `Options.BIN`: 相当于`solc --bin`
  * `Options.INTERFACE`: 相当于`solc --interface`
  * `Options.METADATA`: 相当于`solc --metadata` 
  * `Options.ASTJSON`: 相当于`solc --ast-json`

返回：
* `SolidityCompiler::Result`对象:
  * boolean success: 是否编译成功
  * String errors: 错误或者警告信息，编译失败时保存错误信息，编译成功时保存编译的警告信息
  * String output: 编译结果，具体格式参考`solc`的返回格式

## 返回解析  
```shell
public static CompilationResult CompilationResult::parse(String rawJson) throws IOException 
```
参数:
  - String rawJson: Result.getOutput的值

返回:
 - CompilationResult对象
   - Map<String, ContractMetadata> contracts: 合约文件所有合约的编译结果

## 示例
```java
File socFile = new File("HelloWorld.sol");
SolidityCompiler.Result res = SolidityCompiler.compile(solFile, false, true, ABI, BIN, INTERFACE, METADATA);

if (!res.isFailed())) {
    // 编译成功，解析返回
    CompilationResult result = CompilationResult.parse(res.getOutput());
    // 获取HelloWorld合约的编译结果
    CompilationResult.ContractMetadata meta = result.getContract(“HelloWorld”);
    // abi
    String abi = meta.abi;
    // bin
    String bin = meta.bin;
    // 其他逻辑
} else {
    // 编译失败，res.getError()保存编译失败的原因
}
```
