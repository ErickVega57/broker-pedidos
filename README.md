# broker-pedidos

El programa es un sistema de pedidos que funciona de manera distribuida mediante el patrón broker para la 
comunicación de los componentes.

La solución estará formada por tres componentes ejecutables independientes:

**Cliente.** Proporcionará la interfaz mediante la cual el usuario utilizará las funcionalidades del
sistema. El cliente no deberá conocer la ubicación del servidor que proporciona los servicios.

**Broker.** Actuará como intermediario entre clientes y servidores. Será responsable de mantener el
registro de los servicios disponibles, localizar al servidor que proporciona un servicio y gestionar las
solicitudes de ejecución.

**Servidor.** Proporcionará los servicios correspondientes al sistema de procesamiento de pedidos y
será responsable del acceso a los archivos utilizados para almacenar la información.

## Ejecucion del programa:
Entrar a la carpeta raiz del programa 
```bash
cd brokerPedidos
```
### **Compilar**

**Windows**
```bash
.\mvnw.cmd clean install
```

**Linux/MacOs**

Para compilar si tienes mvn instalado en tu sistema.
```bash
mvn clean install
```
En caso de no tenerlo
```bash
./mvnw clean install
```
### **Ejecutar Broker**

Para ejecutar el cliente se necestian los parámetros `puertoBroker`

ejemplo `9000`

**Windows**
```bash
.\mvnw.cmd -q exec:java -Dexec.mainClass="uady.so.broker.Broker" -Dexec.args="9000"
```

**Linux/MacOs**

Si tienes mvn instalado en tu sistema.
```bash
mvn -q exec:java -Dexec.mainClass="uady.so.broker.Broker" -Dexec.args="9000"
```
En caso de no tenerlo
```bash
./mvnw -q exec:java -Dexec.mainClass="uady.so.broker.Broker" -Dexec.args="9000"
```
### **Ejecutar Servicios**

Para ejecutar el cliente se necestian los parámetros `puertoServicio hostBroker:puertoBroker`

Los nombres de los servicios actuales son:
- `RegistrarPedidos`
- `ConsultarPedidos`
- `ListarPedidos`

ejemplo `9001 localhost:9000`

**Windows**
```bash
.\mvnw.cmd -q exec:java -Dexec.mainClass="uady.so.servidor.servicios.AppNombreServicio" -Dexec.args="9001 localhost:9000"
```

**Linux/MacOs**

Si tienes mvn instalado en tu sistema.
```bash
mvn -q exec:java -Dexec.mainClass="uady.so.servidor.servicios.AppNombreServicio" -Dexec.args="9001 localhost:9000"
```
En caso de no tenerlo
```bash
./mvnw -q exec:java -Dexec.mainClass="uady.so.servidor.servicios.AppNombreServicio" -Dexec.args="9001 localhost:9000"
```

### **Ejecutar Cliente**

Para ejecutar el cliente se necestian los parámetros `hostBroker:puertoBroker`

ejemplo `localhost:9000`

**Windows**
```bash
.\mvnw.cmd -q exec:java -Dexec.mainClass="uady.so.cliente.Cliente" -Dexec.args="localhost:9000"
```

**Linux/MacOs**

Si tienes mvn instalado en tu sistema.
```bash
mvn -q exec:java -Dexec.mainClass="uady.so.cliente.Cliente" -Dexec.args="localhost:9000"
```
En caso de no tenerlo
```bash
./mvnw -q exec:java -Dexec.mainClass="uady.so.cliente.Cliente" -Dexec.args="localhost:9000"
```




