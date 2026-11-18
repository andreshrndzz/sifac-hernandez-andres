================================================================================
BIS06 PROGRAMACIÓN III - SEMANA 11 (LABORATORIO 10)
EVIDENCIA DE EJECUCIÓN, MATRIZ DE FALLOS Y EXPLICACIÓN DE CAMBIO DE REPOSITORIO
Estudiante: Andrés Hernández
Fecha: 17 de noviembre de 2026
================================================================================

1. REGISTRO DE ENTORNO Y ARRANQUE (PARTE 0)
- Versión de Java: Java SE 17+ (Apache NetBeans en Windows)
- Servidor de Padrón: sifac.padron.servidor.ServidorConcurrente en localhost:5051
  (Archivos del TSE leídos en ISO-8859-15; flujos del Socket en UTF-8; retardo 1500 ms)
- Servidor Central Demo: sifac.central.CentralDemo en localhost:5052 (flujos en UTF-8)

2. SALIDAS REALES OBTENIDAS EN CONSOLA DE CAJA (PARTES 2, 3 Y 4.1)
--------------------------------------------------------------------------------
[Caso 1: Cédula inexistente (999999999) con servidor encendido]
Opcion: 1
Cedula del cliente (9 digitos): 999999999
Forma de pago (CONTADO/CREDITO): contado
AVISO | Cliente no registrado

[Caso 2: Cédula existente (104560789) con servidor encendido]
Opcion: 1
Cedula del cliente (9 digitos): 104560789
Forma de pago (CONTADO/CREDITO): contado
CLIENTE | 104560789 | MARTA FLORES ORTIZ
Documento abierto: FC-001 | 2026-11-17 | MARTA FLORES ORTIZ

[Caso 3: Consulta con ServidorConcurrente apagado (Fallo controlado)]
Opcion: 1
Cedula del cliente (9 digitos): 104560789
Forma de pago (CONTADO/CREDITO): contado
PADRON NO DISPONIBLE | Padron no disponible en localhost:5051 (Connection refused: getsockopt). Verifique el servidor e intente de nuevo.

3. SALIDA DE LAS 8 VERIFICACIONES REPRODUCIBLES (PARTE 4 - VerificarPadron)
--------------------------------------------------------------------------------
=== EJECUTANDO 8 VERIFICACIONES DEL ADAPTADOR ===
[OK 1/8] Cedula existente y UTF-8 con tildes: MARÍA JOSÉ NÚÑEZ PÉREZ
[OK 2/8] Cedula inexistente devuelve null (NOTFOUND)
[OK 3/8] Mensaje incompleto rechazado: Respuesta invalida del padron: se esperaban 5 campos y llegaron 3
[OK 4/8] Cedula distinta rechazada: La cedula recibida (203450678) no coincide con la solicitada (104560789)
[OK 5/8] Cierre sin respuesta detectado (no es NOTFOUND): El servidor cerro la conexion sin enviar respuesta (fin de flujo)
[OK 6/8] Timeout de lectura vencido controlado: Padron no disponible en localhost:54365 (Read timed out)
[OK 7/8] Conexion rechazada controlada: Padron no disponible en localhost:54367 (Connection refused: getsockopt)
[OK 8/8] Salto de linea bloqueado antes de abrir socket: La cedula no puede contener saltos de linea
=== RESULTADO: 8/8 VERIFICACIONES CORRECTAS ===

4. SALIDA DE PETICIÓN Y CONFIRMACIÓN AL CENTRAL (PARTE 5 - EnviarDocumento)
--------------------------------------------------------------------------------
1. Primer envio:        CONFIRMADO | F-001 | 1000.00 (ACK|F-001)
2. Repeticion identica: CONFIRMADO | F-001 | 1000.00 (ACK|F-001)
3. Envio con conflicto: PENDIENTE (NO CONFIRMADO) | F-001 | Respuesta: CONFLICTO|F-001

5. MATRIZ DE EVIDENCIA Y FALLOS
--------------------------------------------------------------------------------
- Cédula existente:    CLIENTE | 104560789 | MARTA FLORES ORTIZ (abre factura FC-001).
- Cédula inexistente:  Servidor responde NOTFOUND -> adaptador retorna null -> AVISO | Cliente no registrado.
- Servidor apagado:    ConnectException -> ServicioPadronException -> PADRON NO DISPONIBLE (operación termina y recupera el menú).
- Lectura lenta:       SocketTimeoutException al superar setSoTimeout -> ServicioPadronException ([OK 6/8]).
- Cierre sin respuesta:readLine() devuelve null (EOF) -> ServicioPadronException, no se confunde con NOTFOUND ([OK 5/8]).
- Respuesta inválida:  Trama con != 5 campos o cédula no coincidente -> ServicioPadronException ([OK 3/8] y [OK 4/8]).
- Dos cajas:           Cada consulta abre su propio Socket; ServidorConcurrente atiende en hilos separados sobre padrón de solo lectura.
- Reglas previas:      ServicioFacturacionTest pasa íntegro y ServicioFacturacion no importa java.net.

6. EXPLICACIÓN DEL CAMBIO DE REPOSITORIO Y ARQUITECTURA EN CAPAS
--------------------------------------------------------------------------------
- Punto de inyección: En ConsolaCaja.main() se sustituyó únicamente la línea:
  `RepositorioClientes repoClientes = new RepositorioClientesPadron(host, puerto);`
- Separación de responsabilidades: ServicioFacturacion depende exclusivamente de la interfaz
  RepositorioClientes definida en la Semana 2. No conoce host, puerto ni Socket.
- Diferencia entre NOTFOUND y fallo de red: NOTFOUND es una respuesta válida del servidor que
  produce `null` para que la lógica rechace al cliente no registrado; cualquier error de red o de
  protocolo lanza `ServicioPadronException` para que la presentación informe que el servicio no está
  disponible y permita reintentar.