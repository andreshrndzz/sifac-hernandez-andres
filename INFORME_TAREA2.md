# Informe de Tarea 2 · Consulta masiva al padrón con varios hilos

**Estudiante:** Andrés Hernández  
**Curso:** BIS06 · Programación III  
**Equipo de medición:** Procesador con **16 núcleos lógicos** (`Runtime.getRuntime().availableProcessors()`), sistema operativo Windows 11, ejecutando `ServidorConcurrente` (puerto 5051) y `ClienteMasivo` en la misma máquina (`localhost`) sobre un archivo `cedulas.txt` de 200 cédulas (150 existentes en el padrón y 50 inexistentes).

---

## 1. Tabla de mediciones

*Nota metodológica:* Para realizar las 18 corridas de medición sobre el mismo `ServidorConcurrente` de clase se ajustó la pausa simulada a `10 ms` por consulta, conservando intacta la lógica de atención por hilo y búsqueda binaria.

| Hilos | Corrida 1 | Corrida 2 | Corrida 3 | Promedio | Mejora frente a 1 hilo | Conteo observado (Enc. / No enc. = Total) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **1** | 3197 ms | 3123 ms | 3178 ms | **3166.0 ms** | **— (1.00x)** | 150 / 50 = 200 |
| **2** | 1573 ms | 1571 ms | 1565 ms | **1569.7 ms** | **2.02x** | 146 / 49 = 195 (C1) · 147 / 50 = 197 (C2) · 145 / 49 = 194 (C3) |
| **4** | 784 ms | 770 ms | 795 ms | **783.0 ms** | **4.04x** | 139 / 45 = 184 (C1) · 139 / 46 = 185 (C2) · 135 / 50 = 185 (C3) |
| **8** | 401 ms | 491 ms | 409 ms | **433.7 ms** | **7.30x** | 143 / 49 = 192 (C1) · 136 / 48 = 184 (C2) · 138 / 49 = 187 (C3) |
| **16** | 226 ms | 218 ms | 229 ms | **224.3 ms** | **14.11x** | 143 / 50 = 193 (C1) · 136 / 46 = 182 (C2) · 139 / 48 = 187 (C3) |
| **32** *(extra)* | 139 ms | 127 ms | 118 ms | **128.0 ms** | **24.73x** | 127 / 46 = 173 (C1) · 144 / 50 = 194 (C2) · 149 / 49 = 198 (C3) |

---

## 2. El límite observado en mi máquina

El crecimiento proporcional se rompe de forma apreciable a partir de los **8 hilos** (donde la mejora es de **7.30x** en vez de 8.00x, con una eficiencia del 91 %) y se acentúa en **16 hilos** (**14.11x** en lugar de 16.00x, eficiencia del 88 %) y **32 hilos** (**24.73x** en lugar de 32.00x, cayendo al 77 % de eficiencia). Aunque el tiempo absoluto sigue bajando porque los hilos pasan gran parte del tiempo durmiendo en estado `TIMED_WAITING` (liberando la CPU durante el `sleep`), a partir de **8–16 hilos** el rendimiento por hilo agregado decrece de forma sostenida.

---

## 3. Respuestas a las preguntas de análisis

### 1. Con 4 hilos, ¿la mejora fue de 4 veces? Si no, ¿por qué no?
Con 4 hilos el promedio fue de **783.0 ms** frente a **3166.0 ms** con 1 hilo (una relación de **4.04x** por una leve variación de fondo en la primera corrida de 1 hilo, donde 4 hilos representan el tope de escalamiento casi ideal en esta CPU de 16 núcleos). Sin embargo, en cuanto se sube a 8 y 16 hilos se observa claramente dónde se va el tiempo que impide alcanzar la mejora teórica de $N$ veces:
* **El servidor también crea hilos en la misma máquina:** Al correr cliente y servidor en `localhost`, lanzar $N$ hilos en `ClienteMasivo` obliga a `ServidorConcurrente` a crear otros $N$ hilos `AtencionCliente`. Con 8 hilos clientes hay en realidad 16 hilos de aplicación activos (más los hilos `main` y de la JVM) compitiendo por los mismos **16 núcleos lógicos**.
* **Costo de abrir conexiones TCP y `accept()` secuencial:** Mientras que con 1 hilo se abre un único `Socket`, con $N$ hilos hay que instanciar $N$ objetos `Thread`, hacer $N$ apretones de manos TCP (*handshake*) y pasar por el bucle `servidor.accept()`, que es ejecutado por un único hilo principal en el servidor de manera secuencial antes de delegar cada conexión.
* **Cambios de contexto del planificador:** El sistema operativo invierte tiempo de CPU conmutando hilos entre `RUNNABLE` y `TIMED_WAITING` y gestionando los búferes de entrada/salida de red local.

### 2. ¿A partir de cuántos hilos deja de mejorar en su máquina, y qué cree que lo limita?
La mejora deja de crecer proporcionalmente a partir de los **8 a 16 hilos** (pasando de una eficiencia del 100 % en 4 hilos a 91 % en 8 hilos, 88 % en 16 hilos y 77 % en 32 hilos).  
El factor físico que lo limita son los **16 núcleos lógicos** de mi procesador frente a la duplicación de hilos cliente-servidor: cuando `ClienteMasivo` lanza **8 hilos**, ya existen **16 hilos en ejecución simultánea** en la máquina (8 en el cliente + 8 en el servidor), copando exactamente los 16 núcleos lógicos. Al subir a **16 hilos** (32 hilos totales entre cliente y servidor) y **32 hilos** (64 hilos totales, cuatro veces los núcleos disponibles), ningún núcleo adicional puede ejecutar cómputo ni E/S de sockets en verdadero paralelismo; además, cada hilo atiende apenas 6 o 7 cédulas, por lo que el costo fijo de crear el hilo y abrir/cerrar su socket pesa casi tanto como las consultas mismas.

### 3. Sus hilos comparten el contador de encontradas. ¿Qué podría salir mal y por qué no le salió mal, o sí le salió?
**Sí salió mal en mis corridas a partir de 2 hilos**, y la tabla lo demuestra empíricamente: mientras que con 1 hilo siempre dio **150 encontradas y 50 no encontradas (total 200)**, con 4 hilos dio totales de **184 y 185**, con 16 hilos bajó hasta **182 (136 encontradas y 46 no encontradas)**, y en la primera corrida de 32 hilos contó apenas **173 en total (127 encontradas y 46 no encontradas)**, perdiendo 27 respuestas en el camino.  
Lo que ocurrió es una **condición de carrera (*race condition*)** sobre las variables compartidas `encontradas` y `noEncontradas`. La operación `encontradas++` no es atómica: la JVM primero lee el valor actual de memoria, luego le suma 1 en un registro y finalmente escribe el resultado. Como todos los hilos arrancan casi al mismo instante y cada consulta tarda los mismos `10 ms`, varios hilos despiertan del socket en el mismo milisegundo, leen exactamente el mismo valor viejo de `encontradas` (por ejemplo, `80`), ambos calculan `81` y ambos escriben `81`, perdiéndose un incremento completo.

### 4. Si el servidor tuviera un límite de 10 conexiones simultáneas, ¿qué cambiaría en su cliente?
Si el servidor rechazara o encolara cualquier conexión por encima de 10 clientes activos a la vez, mi cliente no podría instanciar más de 10 hilos que abran un socket cada uno (hacerlo con 16 o 32 hilos provocaría rechazos de conexión o esperas muertas).  
Cambiaría el diseño del cliente para desacoplar la cantidad de cédulas del número de conexiones: **fijaría un tope máximo de 10 hilos trabajadores**, donde cada uno abre una única conexión persistente contra el servidor al iniciar y la reutiliza para consultar múltiples cédulas (repartiendo las 200 cédulas en a lo sumo 10 bloques de 20 cédulas, o haciendo que los 10 hilos vayan tomando cédulas pendientes de una cola compartida hasta agotar las 200 antes de enviar `"SALIR"`).

---

## 4. Propuesta de operación adicional para el servidor de padrón (Sección 3.1)

**Firma propuesta:**  
`public int registrarValidacionCaja(String cedula, String codigoSucursal)`

**Problema real que atiende en SIFAC (3 líneas):**  
Cuando los cajeros de la ferretería consultan una cédula en el servidor de padrón para emitir una factura de crédito o contado, la Junta Administrativa necesita auditar cuántas veces se ha validado cada cédula en el día y desde qué sucursal, para detectar si una misma cédula se está utilizando de forma anómala en varias cajas al mismo tiempo y devolver el conteo acumulado de consultas de ese elector.

**Dificultad concurrente con varios hilos:**  
Dejaría de ser un servidor de solo lectura: si dos cajas en distintas sucursales validan la misma cédula (o actualizan el mismo `Map<String, Integer>` de auditoría en `RepositorioPadron`) simultáneamente desde dos hilos `AtencionCliente`, se produciría una condición de carrera al leer e incrementar el contador del elector y una corrupción estructural del `HashMap` compartido si ambos hilos insertan o redimensionan la tabla al mismo tiempo, obligando a sincronizar el acceso y provocando que aparezca el estado `BLOCKED`.